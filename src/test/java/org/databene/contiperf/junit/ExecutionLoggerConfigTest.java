/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

import static org.junit.Assert.*;

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.log.FileEexecutionLoggerTestUtil;
import org.databene.contiperf.log.FileExecutionLogger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;

/**
 * Tests proper ExecutionLogger handling.<br/><br/>
 * Created: 22.05.2010 18:36:37
 * @since 1.05
 * @author Volker Bergmann
 */
public class ExecutionLoggerConfigTest {
	
	static ExecutionLogger usedLogger;

	@Before
	public void setUp() {
		usedLogger = null;
		FileEexecutionLoggerTestUtil.resetInvocationCount();
	}
	
	// testing default execution logger --------------------------------------------------------------------------------
	
	@Test
	public void testUnconfiguredStandardTest() throws Exception {
		runPlainTestClass(UnconfiguredStandardTest.class);
		assertEquals(FileExecutionLogger.class, usedLogger.getClass());
		assertEquals(2, ((FileExecutionLogger) usedLogger).invocationCount());
	}

	public static class UnconfiguredStandardTest extends ContiPerfTest {
		@Test
		@PerfTest(invocations = 2)
		public void test() {
			usedLogger = contiPerfRule.executionLogger;
		}
	}

	// testing default execution logger --------------------------------------------------------------------------------
	
	@Test
	public void testUnconfiguredCustomTest() throws Exception {
		runPlainTestClass(UnconfiguredCustomTest.class);
		assertEquals(FileExecutionLogger.class, usedLogger.getClass());
		assertEquals(3, ((FileExecutionLogger) usedLogger).invocationCount());
	}

	public static class UnconfiguredCustomTest {
		
		@Rule public ContiPerfRule rule = new ContiPerfRule();
		
		@Test
		@PerfTest(invocations = 3)
		public void test() {
			usedLogger = rule.executionLogger;
		}
	}

	// testing explicit simple test case execution logger --------------------------------------------------------------
	
	@Test
	public void testConfigured() throws Exception {
		runPlainTestClass(ConfiguredTest.class);
		assertEquals(TestExecutionLogger.class, usedLogger.getClass());
		assertEquals(1, ((TestExecutionLogger) usedLogger).id);
		assertEquals(4, ((TestExecutionLogger) usedLogger).invocations);
	}

	public static class ConfiguredTest extends ContiPerfTest {
		
		public ConfiguredTest() {
			super(new TestExecutionLogger(1));
		}

		@Test
		@PerfTest(invocations = 4)
		public void test() {
			usedLogger = contiPerfRule.executionLogger;
		}
	}
	
	
	
	// testing explicit suite execution logger -------------------------------------------------------------------------
	
	@Test
	public void testConfiguredStandardSuite() throws Exception {
		runSuite(ConfiguredStandardSuite.class);
		assertEquals(2, TestExecutionLogger.latestInstance.id);
		assertEquals(3, TestExecutionLogger.latestInstance.invocations);
	}

	@SuiteClasses(UnconfiguredCustomTest.class)
	@PerfTest(invocations = 5)
	public static class ConfiguredStandardSuite extends ContiPerfSuite {
		
		public ConfiguredStandardSuite() {
			super(new TestExecutionLogger(2));
		}

	}

	
	
	// testing explicit suite execution logger -------------------------------------------------------------------------
	
	@Test
	public void testConfiguredCustomSuite() throws Exception {
		runSuite(ConfiguredCustomSuite.class);
		assertEquals(TestExecutionLogger.class, usedLogger.getClass());
		assertEquals(3, TestExecutionLogger.latestInstance.id);
		assertEquals(3, TestExecutionLogger.latestInstance.invocations);
	}

	@SuiteClasses(UnconfiguredCustomTest.class)
	@PerfTest(invocations = 6)
	public static class ConfiguredCustomSuite {
		public ExecutionLogger el = new TestExecutionLogger(3);
	}

	
	
	// private helpers -------------------------------------------------------------------------------------------------
	
	private void runPlainTestClass(Class<?> testClass) throws InitializationError {
		BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(testClass);
		RunNotifier notifier = new RunNotifier();
		runner.run(notifier);
	}
	
	private void runSuite(Class<?> testClass) throws InitializationError {
	    ContiPerfSuiteRunner suite = new ContiPerfSuiteRunner(testClass);
		RunNotifier notifier = new RunNotifier();
		suite.run(notifier);
    }

}
