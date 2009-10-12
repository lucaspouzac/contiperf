/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU Lesser General Public License (LGPL).
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
import org.databene.contiperf.Util;
import org.databene.contiperf.log.FileExecutionLogger;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * TODO Document class.<br/><br/>
 * Created: 12.10.2009 07:36:02
 * @since 0.1
 * @author Volker Bergmann
 */
public class ContiPerfRule implements org.junit.rules.MethodRule {
	
	private static final String SYSPROP_CONFIG_FILENAME = "contiperf.config";
	private static final String DEFAULT_CONFIG_FILENAME = "contiperf.properties";
	
	static {
		readConfig();
	}
	
	private static Map<String, Integer> methodInvocationCounts;
	private static long defaultInvocationCount;
	
	private ExecutionLogger logger;
	
    public ContiPerfRule() {
    	this(new FileExecutionLogger());
    }

	public ContiPerfRule(ExecutionLogger logger) {
	    this.logger = logger;
    }

	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
	    String methodName = methodName(method, target);
		long invocationCount = invocationCount(methodName);
		return new MultiCallStatement(base, invocationCount, methodName, logger);
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
	
	private long invocationCount(String methodName) {
		Integer count = methodInvocationCounts.get(methodName);
		return (count != null ? count : defaultInvocationCount);
	}
	
}
