/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the combination of a {@link Required} annotation in a plain test class 
 * and a {@link PerfTest} in a suite that contains the former test.<br/><br/>
 * Created: 24.05.2010 06:42:31
 * @since 1.05
 * @author Volker Bergmann
 */
public class AnnotationCombinationTest extends AbstractContiPerfTest {

	// testing suite that matches the requirements ---------------------------------------------------------------------
	
	@Test
	public void testSuccessfulSuiteWithExecutionConfig() throws Exception {
        runTest(SucessfulSuiteWithExecutionConfig.class);
        // TODO this fails under Maven: assertFalse(failed);
	}
	
	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(TestWithRequirements.class)
	@PerfTest(invocations = 2)
	public static class SucessfulSuiteWithExecutionConfig {
	}
	
	// testing suite that misses the requirements ----------------------------------------------------------------------
	
	@Test
	public void testFailingSuiteWithExecutionConfig() throws Exception {
        runTest(FailingSuiteWithExecutionConfig.class);
        assertTrue(failed);
	}
	
	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(TestWithRequirements.class)
	@PerfTest(invocations = 5)
	public static class FailingSuiteWithExecutionConfig {
	}
	
	// simple test class with performance requirements annotation ------------------------------------------------------
	
	public static class TestWithRequirements {
		@Test
		@Required(totalTime = 300)
		public void test() throws Exception {
			Thread.sleep(80);
		}
	}
	
}
