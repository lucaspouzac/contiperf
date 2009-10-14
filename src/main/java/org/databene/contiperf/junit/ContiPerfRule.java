/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU Lesser General Public License (LGPL), Eclipse Public License (EPL) 
 * and the BSD License.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.contiperf.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Util;
import org.databene.contiperf.log.FileExecutionLogger;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Implements the JUnit {@link MethodRule} interface 
 * for adding performance test features to test calls.
 * 
 * <p>for activating it, add an attribute of this class 
 * to your test class, e.g.:
 * <pre>
 * public class SimpleTest {
 *     @Rule
 *     public ContiPerfRule i = new ContiPerfRule();
 * 
 *     @Test
 *     public void sleepAWhile() throws Exception {
 *         Thread.sleep(100);
 *     }
 * }
 * </pre> 
 * ContiPerf will then intercept each test method call
 * and optionally cause multiple invocations and check 
 * total execution time against a time limit.</p>
 * 
 * <p>invocation counts and time limits can be configured 
 * by Java annotations, e.g.
 * <pre>
 * @Test(timeout = 300)
 * @PerfTest(invocations = 5, timeLimit = 500)
 * public void sleepALittleLonger() throws Exception {
 *     System.out.print('x');
 *     Thread.sleep(200);
 * }
 * </pre>
 * </p>
 * 
 * <p>For enabling different test settings, the invocation count
 * values can be configured in a properties file <code>contiperf.properties</code>
 * which assigns the invocation count to the fully qualified method name, e.g.
 * <pre>
 * org.databene.contiperf.junit.SimpleTest.sleepAWhile=3
 * org.databene.contiperf.junit.SimpleTest.sleepALittleLonger=2
 * </pre>
 * If the properties file exists, it overrides the annotation values.</p>
 * 
 * <p>By default, the execution times are written to the CSV file  
 * <code>target/contiperf/contiperf.log</code>. They have four columns,
 * listing the
 * <ol>
 *     <li>fully qualified method name</li>
 *     <li>total execution time</li>
 *     <li>invocation count</li>
 *     <li>start time in milliseconds since 1970-01-01</li>
 * </ol></p>
 * 
 * <p>For reusing integration tests as performance tests, you can globally 
 * turn ContiPerf off by setting the System property 
 * <code>contiperf.active=false</code>. 
 * </p>
 * <br/><br/>
 * Created: 12.10.2009 07:36:02
 * @since 0.1
 * @author Volker Bergmann
 */
public class ContiPerfRule implements MethodRule {
	
	public static final String SYSPROP_ACTIVE = "contiperf.active";
	public static final String SYSPROP_CONFIG_FILENAME = "contiperf.config";
	
	private static final String DEFAULT_CONFIG_FILENAME = "contiperf.properties";
	
	private static Map<String, Integer> methodInvocationCounts;
	private static Map<String, Integer> methodTimeouts;
	private static int defaultInvocationCount;
	
	private ExecutionLogger logger;
	
	// initialization --------------------------------------------------------------------------------------------------
	
	static {
		readConfig();
	}
	
    public ContiPerfRule() {
    	this(new FileExecutionLogger());
    }

	public ContiPerfRule(ExecutionLogger logger) {
	    this.logger = logger;
    }
	
	// MethodRule interface implementation -----------------------------------------------------------------------------

	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		if (!active())
			return base;
	    String methodName = methodName(method, target);
		int invocationCount = invocationCount(method, methodName);
		Integer timeLimit = timeout(method, methodName);
		return new MultiCallStatement(base, invocationCount, timeLimit, methodName, logger);
    }

	// helpers ---------------------------------------------------------------------------------------------------------

	private boolean active() {
		String sysprop = System.getProperty(SYSPROP_ACTIVE);
		return (sysprop == null || !"false".equals(sysprop.toLowerCase()));
    }

	private static void readConfig() {
	    String filename = configFileName();
    	Properties props = new Properties();
	    InputStream in = inputStream(filename);
	    if (in != null) {
	    	try {
	    		props.load(in);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		Util.close(in);
	    	}
	    }
	    defaultInvocationCount = 1;
		methodInvocationCounts = new HashMap<String, Integer>();
		for (Map.Entry<?, ?> entry : props.entrySet()) {
	        String methodName = entry.getKey().toString();
	        int invocationCount = Integer.parseInt(entry.getValue().toString());
	        if ("default".equals(methodName))
	        	defaultInvocationCount = invocationCount;
	        else
	        	methodInvocationCounts.put(methodName, invocationCount);
        }
		methodTimeouts = new HashMap<String, Integer>();
		// TODO populate methodTimeouts
    }
	
	private static InputStream inputStream(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			try {
	            return new FileInputStream(file);
            } catch (FileNotFoundException e) {
	            // This is not supposed to happen. But if it does, we fall back to the context ClassLoader!
            }
		}
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
    }

	private static String configFileName() {
		String filename = System.getProperty(SYSPROP_CONFIG_FILENAME);
		if (filename == null || filename.trim().length() == 0)
			filename = DEFAULT_CONFIG_FILENAME;
		return filename;
	}

	private String methodName(FrameworkMethod method, Object target) {
		return target.getClass().getName() + '.' + method.getName(); 
		// no need to check signature: JUnit test methods have no parameters
	}
	
	private int invocationCount(FrameworkMethod method, String methodName) {
		Integer count = methodInvocationCounts.get(methodName);
		if (count != null)
			return count;
		PerfTest annotation = method.getAnnotation(PerfTest.class);
		if (annotation != null && annotation.invocations() > 0)
			return annotation.invocations();
		return defaultInvocationCount;
	}
	
	private Integer timeout(FrameworkMethod method, String methodName) {
	    Integer timeout = methodTimeouts.get(methodName);
		if (timeout == null) {
			PerfTest annotation = method.getAnnotation(PerfTest.class);
			if (annotation != null && annotation.timeLimit() > 0)
				timeout = annotation.timeLimit();
		}
	    return timeout;
    }

}
