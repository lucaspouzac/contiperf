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
import org.databene.contiperf.log.FileExecutionLoggerTestUtil;
import org.databene.contiperf.report.LoggerModuleAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests proper ExecutionLogger handling.<br/><br/>
 * Created: 22.05.2010 18:36:37
 * @since 1.05
 * @author Volker Bergmann
 */
@SuppressWarnings("deprecation")
public class ExecutionLoggerConfigTest extends AbstractContiPerfTest {
	
	static ExecutionLogger usedLogger;

	@Override
    @Before
	public void setUp() {
		super.setUp();
		usedLogger = null;
		FileExecutionLoggerTestUtil.resetInvocationCount();
	}

	

	// testing explicit simple test case execution logger --------------------------------------------------------------
	
	@Test
	public void testConfigured() throws Exception {
		runTest(ConfiguredTest.class);
		assertEquals(ExecutionTestLogger.class, usedLogger.getClass());
		assertEquals(1, ((ExecutionTestLogger) usedLogger).id);
		assertEquals(4, ((ExecutionTestLogger) usedLogger).invocations);
	}

	public static class ConfiguredTest {
		
		@Rule public ContiPerfRule rule = new ContiPerfRule(new ExecutionTestLogger(1));

		@Test
		@PerfTest(invocations = 4)
		public void test() {
			usedLogger = ((LoggerModuleAdapter) rule.getContext().getReportModules().get(0)).getLogger();
		}
	}
	
	
	
	// testing explicit suite execution logger -------------------------------------------------------------------------
	
	@Test
	public void testConfiguredSuite() throws Exception {
		runTest(ConfiguredSuite.class);
		assertEquals(ExecutionTestLogger.class, usedLogger.getClass());
		assertEquals(3, ExecutionTestLogger.latestInstance.id);
		assertEquals(3, ExecutionTestLogger.latestInstance.invocations);
	}

	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(UnconfiguredTest.class)
	@PerfTest(invocations = 6)
	public static class ConfiguredSuite {
		public ExecutionLogger el = new ExecutionTestLogger(3);
	}

	public static class UnconfiguredTest {
		
		@Rule public ContiPerfRule rule = new ContiPerfRule();
		
		@Test
		@PerfTest(invocations = 3)
		public void test() {
			usedLogger = ((LoggerModuleAdapter) rule.getContext().getReportModules().get(0)).getLogger();
		}
	}

}
