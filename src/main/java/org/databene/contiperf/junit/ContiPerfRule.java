/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import java.lang.annotation.Annotation;

import org.databene.contiperf.Config;
import org.databene.contiperf.ExecutionConfig;
import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.contiperf.Required;
import org.databene.contiperf.log.FileExecutionLogger;
import org.databene.contiperf.util.ContiPerfUtil;
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
 * <p>For reusing integration tests as performance tests, you can suppress
 * ContiPerf execution by setting the System property <code>contiperf.active=false</code>. 
 * </p>
 * <br/><br/>
 * Created: 12.10.2009 07:36:02
 * @since 1.0
 * @author Volker Bergmann
 */
public class ContiPerfRule implements MethodRule {
	
	private ExecutionConfig defaultExecutionConfig;
	private PerformanceRequirement defaultRequirements;
	private final ExecutionLogger logger;
	
	// initialization --------------------------------------------------------------------------------------------------
	
   public ContiPerfRule() {
	    this(new FileExecutionLogger());
    }

	public ContiPerfRule(ExecutionLogger executionLogger) {
	    this(null, executionLogger);
    }

	public ContiPerfRule(Class<?> suiteClass, ExecutionLogger logger) {
		if (suiteClass != null) {
			defaultExecutionConfig = configurePerfTest(suiteClass.getAnnotation(PerfTest.class), suiteClass.getName());
			defaultRequirements = ContiPerfUtil.mapRequired(suiteClass.getAnnotation(Required.class));
		}
		this.logger = logger;
    }
	
	// MethodRule interface implementation -----------------------------------------------------------------------------

	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		Config config = Config.instance();
		if (!config.active())
			return base;
	    String testId = methodName(method, target);
		return new PerfTestStatement(base, testId, executionConfig(method, testId), 
				requirements(method, testId), logger);
    }
	
	// helpers ---------------------------------------------------------------------------------------------------------

	private static String methodName(FrameworkMethod method, Object target) {
		return target.getClass().getName() + '.' + method.getName(); 
		// no need to check signature: JUnit test methods have no parameters
	}
	
	private ExecutionConfig executionConfig(FrameworkMethod method, String methodName) {
		PerfTest annotation = annotationOfMethodOrClass(method, PerfTest.class);
        if (annotation != null)
        	return configurePerfTest(annotation, methodName);
        if (defaultExecutionConfig != null)
        	return defaultExecutionConfig;
        return new ExecutionConfig(1);
	}

	private PerformanceRequirement requirements(FrameworkMethod method, @SuppressWarnings("unused") String testId) {
		// TODO v1.x make use of config file
		Required annotation = annotationOfMethodOrClass(method, Required.class);
		if (annotation != null)
			return ContiPerfUtil.mapRequired(annotation);
        if (defaultRequirements != null)
        	return defaultRequirements;
		return null;
    }

	private static <T extends Annotation> T annotationOfMethodOrClass(FrameworkMethod method, Class<T> annotationClass) {
		T methodAnnotation = method.getAnnotation(annotationClass);
		if (methodAnnotation != null)
			return methodAnnotation;
		T classAnnotation = method.getMethod().getDeclaringClass().getAnnotation(annotationClass);
        return classAnnotation;
	}
	
	private static ExecutionConfig configurePerfTest(PerfTest annotation, String testId) {
		ExecutionConfig config = ContiPerfUtil.mapPerfTestAnnotation(annotation);
		if (annotation == null)
			config = new ExecutionConfig(1);
		int count = Config.instance().getInvocationCount(testId);
		if (count >= 0)
			config.setInvocations(count);
		return config;
    }


}
