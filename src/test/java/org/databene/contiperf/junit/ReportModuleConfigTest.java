/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.report.HtmlReportModule;
import org.databene.contiperf.report.ReportContext;
import org.databene.contiperf.report.ReportModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Integration test for ContiPerf's {@link ReportModule} support.<br/><br/>
 * Created: 16.01.2011 14:06:10
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class ReportModuleConfigTest extends AbstractContiPerfTest {
	
	static ReportContext usedContext;

	@Override
    @Before
	public void setUp() {
		super.setUp();
		usedContext = null;
	}

	// testing default execution logger --------------------------------------------------------------------------------

	@Test
	public void testUnconfiguredTest() throws Exception {
		runTest(UnconfiguredTest.class);
		assertNotNull(usedContext.getReportModule(HtmlReportModule.class));
	}

	public static class UnconfiguredTest {
		
		@Rule public ContiPerfRule rule = new ContiPerfRule();
		
		@Test
		@PerfTest(invocations = 3)
		public void test() {
			usedContext = rule.context;
		}
	}

	// testing explicit simple test case execution logger --------------------------------------------------------------

	@Test
	public void testParamConfigured() throws Exception {
		runTest(ParamConfiguredTest.class);
		ExecutionTestModule logger = usedContext.getReportModule(ExecutionTestModule.class);
		assertEquals(1, logger.id);
		assertEquals(4, logger.invocations);
	}

	public static class ParamConfiguredTest {
		
		@Rule public ContiPerfRule rule = new ContiPerfRule(new ExecutionTestModule(1));

		@Test
		@PerfTest(invocations = 4)
		public void test() {
			usedContext = rule.context;
		}
	}

	@Test
	public void testAttributeConfigured() throws Exception {
		runTest(AttributeConfiguredTest.class);
		ExecutionTestModule logger = usedContext.getReportModule(ExecutionTestModule.class);
		assertEquals(2, logger.id);
		assertEquals(5, logger.invocations);
	}

	public static class AttributeConfiguredTest {
		
		public ReportModule module = new ExecutionTestModule(2);
		@Rule public ContiPerfRule rule = new ContiPerfRule();

		@Test
		@PerfTest(invocations = 5)
		public void test() {
			usedContext = rule.context;
		}
	}

	
	
	// testing explicit suite execution logger -------------------------------------------------------------------------
	
	@Test
	public void testConfiguredSuite() throws Exception {
		runTest(ConfiguredSuite.class);
		assertNotNull("ExecutionTestModule was not used", ExecutionTestModule.latestInstance);
		assertEquals("ExecutionTestModule was not properly initialized:", 4, ExecutionTestModule.latestInstance.id);
		assertEquals("ExecutionTestModule was not called properly:", 3, ExecutionTestModule.latestInstance.invocations);
	}

	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(UnconfiguredTest.class)
	public static class ConfiguredSuite {
		public ReportModule el = new ExecutionTestModule(4);
	}

}
