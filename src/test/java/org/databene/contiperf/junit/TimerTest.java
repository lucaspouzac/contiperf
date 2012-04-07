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

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.timer.ConstantTimer;
import org.databene.contiperf.timer.RandomTimer;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests initialization and application of timers.<br/><br/>
 * Created: 06.04.2012 17:57:32
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class TimerTest {

	@Rule public ContiPerfRule rule = new ContiPerfRule();
	
	private long recentMillis = -1;

	@Test
	@PerfTest(invocations = 6, timer = ConstantTimer.class, timerParams = { 200 })
	public void testConstant() {
		long currentMillis = System.currentTimeMillis();
		System.out.println("testConstant()");
		if (recentMillis != -1) {
			long elapsedMillis = currentMillis - recentMillis;
			assertTrue("expected a delay of at least 180 ms, but measured " + elapsedMillis + " ms", elapsedMillis > 180);
			assertTrue("expected a delay of at most 220 ms, but measured " + elapsedMillis + " ms", elapsedMillis < 220);
		}
		recentMillis = currentMillis;
	}
	
	
	
	private long randomStartMillis = -1;

	@Test
	@PerfTest(invocations = 20, threads = 3, rampUp = 1000, timer = RandomTimer.class, timerParams = { 200, 400 })
	public void testRandom() {
		long currentMillis = System.currentTimeMillis();
		if (randomStartMillis == -1)
			randomStartMillis = currentMillis;
		long offset = currentMillis - randomStartMillis;
		System.out.println("testRandom(" + Thread.currentThread().getName() + ", " + offset + ")");
	}
	
}
