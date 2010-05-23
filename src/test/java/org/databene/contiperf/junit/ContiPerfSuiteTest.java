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

import java.util.concurrent.atomic.AtomicInteger;

import org.databene.contiperf.PerfTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;

/**
 * Tests {@link ContiPerfSuiteRunner} processing.<br/><br/>
 * Created: 02.05.2010 09:18:31
 * @since 1.05
 * @author Volker Bergmann
 */
public class ContiPerfSuiteTest {
	
	@Before
	public void setUp() {
		uCount.set(0);
		cCount.set(0);
		//pCount.set(0);
	}
	
	@Test
	public void test() {
		
	}
	
	@Test
	public void testUnconfiguredSuiteForUnconfiguredTest() throws InitializationError {
		runSuite(UnconfiguredSuiteForUnconfiguredTest.class);
		assertEquals(1, uCount.get());
		assertEquals(0, cCount.get());
	}

	@Test
	public void testConfiguredSuiteForUnconfiguredTest() throws InitializationError {
		runSuite(ConfiguredSuiteForUnconfiguredTest.class);
		assertEquals(11, uCount.get());
		assertEquals(0, cCount.get());
	}

	@Test
	public void testUnconfiguredSuiteForConfiguredMethodTest() throws InitializationError {
		runSuite(UnconfiguredSuiteForConfiguredMethodTest.class);
		assertEquals(0, uCount.get());
		assertEquals(2, cCount.get());
	}

	@Test
	public void testConfiguredSuiteForConfiguredMethodTest() throws InitializationError {
		runSuite(ConfiguredSuiteForConfiguredMethodTest.class);
		assertEquals(0, uCount.get());
		assertEquals(2, cCount.get());
	}

	@Test
	public void testUnconfiguredSuiteForConfiguredClassTest() throws InitializationError {
		runSuite(UnconfiguredSuiteForConfiguredClassTest.class);
		assertEquals(0, uCount.get());
		assertEquals(3, cCount.get());
	}

	@Test
	public void testConfiguredSuiteForConfiguredClassTest() throws InitializationError {
		runSuite(ConfiguredSuiteForConfiguredClassTest.class);
		assertEquals(0, uCount.get());
		assertEquals(3, cCount.get());
	}

	@Test
	public void testUnconfiguredSuiteForConfiguredClassAndMethodTest() throws InitializationError {
		runSuite(UnconfiguredSuiteForConfiguredClassAndMethodTest.class);
		assertEquals(0, uCount.get());
		assertEquals(7, cCount.get());
	}

	@Test
	public void testConfiguredSuiteForConfiguredClassAndMethodTest() throws InitializationError {
		runSuite(ConfiguredSuiteForConfiguredClassAndMethodTest.class);
		assertEquals(0, uCount.get());
		assertEquals(7, cCount.get());
	}

/*	
	@Test
	public void testConfiguredSuiteForParameterizedTest() throws InitializationError {
		runSuite(ConfiguredSuiteForParametrizedTest.class);
		assertEquals(0, uCount.get());
		assertEquals(0, cCount.get());
		assertEquals(9, pCount.get());
	}
*/
	// helper methods --------------------------------------------------------------------------------------------------

	private void runSuite(Class<?> testClass) throws InitializationError {
	    ContiPerfSuiteRunner suite = new ContiPerfSuiteRunner(testClass);
		RunNotifier notifier = new RunNotifier();
		suite.run(notifier);
    }

	// tested classes and their invocation counters --------------------------------------------------------------------
	
	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(UnconfiguredTest.class)
	public static class UnconfiguredSuiteForUnconfiguredTest {
	}
	
	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(UnconfiguredTest.class)
	@PerfTest(invocations = 11, threads = 2)
	public static class ConfiguredSuiteForUnconfiguredTest {
	}
	
	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(ConfiguredMethodTest.class)
	public static class UnconfiguredSuiteForConfiguredMethodTest {
	}
	
	@SuiteClasses(ConfiguredMethodTest.class)
	@PerfTest(invocations = 13, threads = 2)
	public static class ConfiguredSuiteForConfiguredMethodTest extends ContiPerfSuite {
	}
	
	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(ConfiguredClassTest.class)
	public static class UnconfiguredSuiteForConfiguredClassTest {
	}
	
	@SuiteClasses(ConfiguredClassTest.class)
	@PerfTest(invocations = 17, threads = 2)
	public static class ConfiguredSuiteForConfiguredClassTest extends ContiPerfSuite {
	}
	
	@RunWith(ContiPerfSuiteRunner.class)
	@SuiteClasses(ConfiguredClassAndMethodTest.class)
	public static class UnconfiguredSuiteForConfiguredClassAndMethodTest {
	}
	
	@SuiteClasses(ConfiguredClassAndMethodTest.class)
	@PerfTest(invocations = 19, threads = 2)
	public static class ConfiguredSuiteForConfiguredClassAndMethodTest extends ContiPerfSuite {
	}
	
/* 
	@RunWith(ContiPerfSuite.class)
	@SuiteClasses(ParameterizedTest.class)
	@PerfTest(invocations = 3, threads = 2)
	public static class ConfiguredSuiteForParameterizedTest {
	}
*/
	static volatile AtomicInteger uCount = new AtomicInteger();
	static volatile AtomicInteger cCount = new AtomicInteger();
//	static volatile AtomicInteger pCount = new AtomicInteger();
	
	
	public static class UnconfiguredTest {
		@Test
		public void test() throws Exception {
			uCount.incrementAndGet();
		}
	}

	
	
	public static class ConfiguredMethodTest extends ContiPerfTest {
		@Test
		@PerfTest(invocations = 2)
		public void test() throws Exception {
			cCount.incrementAndGet();
		}
	}
	
	
	
	@PerfTest(invocations = 3)
	public static class ConfiguredClassTest extends ContiPerfTest {
		@Test
		public void test() throws Exception {
			cCount.incrementAndGet();
		}
	}
	
	
	
	@PerfTest(invocations = 5)
	public static class ConfiguredClassAndMethodTest extends ContiPerfTest {
		@Test
		@PerfTest(invocations = 7)
		public void test() throws Exception {
			cCount.incrementAndGet();
		}
	}
	
	
	
/*	TODO support parameterized tests
	@RunWith(Parameterized.class)
	public static class ParameterizedTest {
		
		public ParameterizedTest(@SuppressWarnings("unused") Integer n) { }
		
		@SuppressWarnings("unchecked")
        @Parameters
		public static Collection params() {
			return Arrays.asList(new Object[][] { { 1 }, { 2 } });
		 }

		@Test
		public void test() {
			pCount.incrementAndGet();
			System.out.println(pCount);
		}
	}	
*/
}
