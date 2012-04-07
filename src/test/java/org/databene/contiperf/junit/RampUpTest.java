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

import static org.junit.Assert.assertTrue;

import org.databene.contiperf.PerfTest;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the ramp-up behavior of ContiPerf.<br/><br/>
 * Created: 06.04.2012 16:27:37
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class RampUpTest {

	@Rule public ContiPerfRule rule = new ContiPerfRule();
	
	
	
	private static long firstInvCountMillis = -1;
	private static long lastInvCountMillis = -1;
	
	@Test
	@PerfTest(invocations = 3, threads = 3, rampUp = 500)
	public void testRampUp_count() throws InterruptedException {
		long currentTimeMillis = System.currentTimeMillis();
		if (firstInvCountMillis == -1)
			firstInvCountMillis = currentTimeMillis;
		lastInvCountMillis = currentTimeMillis;
		System.out.println("testRampUp_count()");
		Thread.sleep(2000);
	}

	@AfterClass
	public static void verifyRampUp_count() {
		String message = "expected an accumulated ramp-up and execution time of at least 980 ms, " +
				"but measured " + (lastInvCountMillis - firstInvCountMillis) + " ms";
		assertTrue(message, lastInvCountMillis - firstInvCountMillis > 980);
	}
	
	
	
	private static long firstInvDurationMillis = -1;
	private static long lastInvDurationMillis = -1;
	
	@Test
	@PerfTest(duration = 1000, threads = 3, rampUp = 500)
	public void testRampUp_duration() throws InterruptedException {
		long currentTimeMillis = System.currentTimeMillis();
		if (firstInvDurationMillis == -1)
			firstInvDurationMillis = currentTimeMillis;
		lastInvDurationMillis = currentTimeMillis;
		System.out.println("testRampUp_duration()");
		Thread.sleep(300);
	}

	@AfterClass
	public static void verifyRampUp_duration() {
		String message = "expected an accumulated ramp-up and execution time of at least 1800 ms, " +
				"but measured " + (lastInvDurationMillis - firstInvDurationMillis) + " ms";
		assertTrue(message, lastInvDurationMillis - firstInvDurationMillis > 1800);
	}
	
}
