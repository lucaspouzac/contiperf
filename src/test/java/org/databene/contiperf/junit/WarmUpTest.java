/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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
import org.databene.contiperf.Required;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests handling of warmUp settings.<br/><br/>
 * Created: 07.04.2012 08:00:01
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class WarmUpTest {
	
	@Rule public ContiPerfRule rule = new ContiPerfRule();
	
	/* TODO automate these tests - each one is expected to throw a PerfTestExecutionError, which cannot be checked by @Test(expected)
	
	@Test
	@PerfTest(warmUp = 1000, duration = 500)
	public void testFinishDuringWarmup_single_duration() {
	}
	
	@Test
	@PerfTest(warmUp = 1000, threads = 5, duration = 500)
	public void testFinishDuringWarmup_multi_duration() {
	}
	
	@Test
	@PerfTest(warmUp = 1000, invocations = 3)
	public void testFinishDuringWarmup_single_count() {
	}
	
	@Test
	@PerfTest(warmUp = 1000, threads = 5, invocations = 10)
	public void testFinishDuringWarmup_multi_count() {
	}
	
	*/
	
	// testing warm-up on single-threaded timed tests ------------------------------------------------------------------
	
	static long timedSingleThreadedStart = -1;
	static long timedSingleThreadedEnd = -1;
	static volatile AtomicInteger timedSingleThreadedInvocations = new AtomicInteger();
	
	@Test
	@PerfTest(duration = 1000, warmUp = 500)
	public void testTimedSingleThreaded() throws Exception {
		long current = System.currentTimeMillis();
		timedSingleThreadedInvocations.incrementAndGet();
		if (timedSingleThreadedStart == -1)
			timedSingleThreadedStart = current;
		timedSingleThreadedEnd = current;
		System.out.println(Thread.currentThread().getName() + ": " + (current - timedSingleThreadedStart));
		Thread.sleep(50);
	}
	
	@AfterClass
	public static void verifyTimedSingleThreaded() {
		verifyExecution("testTimedSingleThreaded()", 950, timedSingleThreadedEnd - timedSingleThreadedStart,
				18, 22, timedSingleThreadedInvocations.get());
	}

	// concurrent throughput testing -----------------------------------------------------------------------------------
	
	static long throughputMeasurementStart = -1;
	static long throughputMeasurementEnd = -1;
	static volatile AtomicInteger throughputInvocations = new AtomicInteger();
	
	/**
	 * Testing throughput with 3 threads after a ramp-up of 2*500 ms with a warm-up of 1000 ms.
	 * The required throughput is only achieved when all threads are running. So the warm-up
	 * assures that measurement begins after 1000 ms which is the cumulated ram-up time for all 
	 * threads. The cumulated ramp-up adds to the duration, so the test is expected to run for 
	 * about 2 seconds.
	 */
	@Test
	@PerfTest(threads = 3, duration = 1000, rampUp = 500, warmUp = 1000)
	@Required(throughput = 50)
	public void testThroughputMeasurement() throws Exception {
		long current = System.currentTimeMillis();
		throughputInvocations.incrementAndGet();
		if (throughputMeasurementStart == -1)
			throughputMeasurementStart = current;
		throughputMeasurementEnd = current;
		System.out.println(Thread.currentThread().getName() + ": " + (current - throughputMeasurementStart));
		Thread.sleep(50);
	}
	
	@AfterClass
	public static void verifyThroughputRun() {
		verifyExecution("testThroughputMeasurement()", 1900, throughputMeasurementEnd - throughputMeasurementStart, 
				87, 93, throughputInvocations.get());
	}
	
	// private helpers -------------------------------------------------------------------------------------------------
	
	private static void verifyExecution(String testName, int expectedMinDuration, long actualDuration,
			int expectedMinInvocations, int expectedMaxInvocations, int actualInvocations) {
		String message = testName + " is expected to run at least for " + expectedMinDuration + " ms, " +
				"but it actually has run for " + actualDuration + " ms.";
		assertTrue(message, actualDuration >= expectedMinDuration);
		assertTrue("Expected at least " + expectedMinInvocations + " invocations, but measured " + actualInvocations, 
				actualInvocations >= expectedMinInvocations);
		assertTrue("Expected at most " + expectedMaxInvocations + " invocations, but measured " + actualInvocations, 
				actualInvocations <= expectedMaxInvocations);
	}
	
}
