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

package org.databene.contiperf.timer;

import static org.junit.Assert.assertTrue;

import org.databene.contiperf.WaitTimer;
import org.junit.Test;

/**
 * TODO Document class.<br/><br/>
 * Created: 06.04.2012 18:19:40
 * @since TODO version
 * @author Volker Bergmann
 */
public class CumulatedTimerTest {

	@Test
	public void testEmptyInitialization() throws Exception {
		WaitTimer timer = CumulatedTimer.class.newInstance();
		timer.init(new double[0]);
		for (int i = 0; i < 1000; i++)
			assertRange(500, 1500, timer.getWaitTime());
	}

	@Test
	public void testUnderInitialization() throws Exception {
		WaitTimer timer = CumulatedTimer.class.newInstance();
		timer.init(new double[] { 2000 });
		for (int i = 0; i < 1000; i++)
			assertRange(2000, 3000, timer.getWaitTime());
	}

	@Test
	public void testNormalInitialization() throws Exception {
		WaitTimer timer = CumulatedTimer.class.newInstance();
		timer.init(new double[] { 2000, 2500 });
		for (int i = 0; i < 1000; i++)
			assertRange(2000, 2500, timer.getWaitTime());
	}

	@Test
	public void testOverInitialization() throws Exception {
		WaitTimer timer = CumulatedTimer.class.newInstance();
		timer.init(new double[] { 2000, 2500, 3000 });
		for (int i = 0; i < 1000; i++)
			assertRange(2000, 2500, timer.getWaitTime());
	}

	private void assertRange(int minExpected, int maxExpected, int waitTime) {
		assertTrue(minExpected <= waitTime && waitTime <= maxExpected);
	}
	
}
