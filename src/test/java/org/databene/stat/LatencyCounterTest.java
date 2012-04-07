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

package org.databene.stat;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests the {@link LatencyCounter}.<br/><br/>
 * Created: 26.02.2012 18:31:16
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class LatencyCounterTest {
	
	@Test(expected = IllegalStateException.class)
	public void testStartTwice() {
		LatencyCounter counter = new LatencyCounter();
		counter.start();
		counter.start();
	}
	
	@Test(expected = IllegalStateException.class)
	public void testStopTwice() {
		LatencyCounter counter = new LatencyCounter();
		counter.start();
		counter.stop();
		counter.stop();
	}
	
	@Test
	public void testPercentileAboveLatency() {
		LatencyCounter counter = new LatencyCounter();
		counter.start();
		for (int i = 25; i <= 125; i += 25)
			counter.addSample(i);
		counter.stop();
		assertEquals(100., counter.percentileAboveLatency(0), 0.);
		assertEquals(80., counter.percentileAboveLatency(25), 0.);
		assertEquals(40., counter.percentileAboveLatency(99), 0.);
		assertEquals(20., counter.percentileAboveLatency(100), 0.);
		assertEquals(20., counter.percentileAboveLatency(124), 0.);
		assertEquals(0., counter.percentileAboveLatency(125), 0.);
		assertEquals(0., counter.percentileAboveLatency(126), 0.);
	}
	
}
