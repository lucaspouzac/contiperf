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

package org.databene.contiperf;

import static org.junit.Assert.*;

import org.databene.stat.CounterRepository;
import org.databene.stat.LatencyCounter;
import org.junit.After;
import org.junit.Test;

/**
 * Tests the {@link StopWatch}.<br/><br/>
 * Created: 14.01.2011 11:33:37
 * @since 1.08
 * @author Volker Bergmann
 */
public class StopWatchTest {

	private static final String NAME = "StopWatchTest";
	
	@After
	public void tearDown() {
		CounterRepository.getInstance().clear();
	}
	
	@Test
	public void testSingleCall() throws InterruptedException {
		sleepTimed(50);
		assertEquals(1, getCounter().sampleCount());
	}

	@Test
	public void testSubsequentCalls() throws InterruptedException {
		sleepTimed(50);
		sleepTimed(50);
		sleepTimed(50);
		LatencyCounter counter = getCounter();
		assertEquals(3, counter.sampleCount());
		assertTrue(counter.minLatency() >= 39);
		assertTrue(counter.minLatency() < 100);
		assertTrue(counter.averageLatency() >= 39);
		assertTrue(counter.averageLatency() < 100);
	}

	@Test
	public void testParallelCalls() throws InterruptedException {
		// TODO v2.0.1 implement testParallelCalls()
	}
	
	@Test(expected = RuntimeException.class)
	public void testMultiStop() {
		StopWatch watch = new StopWatch(NAME);
		watch.stop();
		watch.stop();
	}

	private void sleepTimed(int delay) throws InterruptedException {
		StopWatch watch = new StopWatch(NAME);
		Thread.sleep(delay);
		watch.stop();
	}
	
	private LatencyCounter getCounter() {
		return CounterRepository.getInstance().getCounter(NAME);
	}

}
