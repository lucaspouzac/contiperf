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

import org.databene.contiperf.Clock;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.clock.CpuClock;
import org.databene.contiperf.clock.SystemClock;
import org.databene.contiperf.clock.UserClock;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the usage of multiple {@link Clock}s.<br/><br/>
 * Created: 24.05.2012 09:30:45
 * @since 2.2.0
 * @author Volker Bergmann
 */
public class MultiClockTest {
	
	@Rule public ContiPerfRule rule = new ContiPerfRule();
	
	@Test
	@PerfTest(invocations = 10, clocks = { SystemClock.class, UserClock.class, CpuClock.class })
	@Required(throughput = 2, totalTime = 10000, max = 1000, 
			average = 300, median = 301, percentiles = "55:302,77:303", percentile90 = 304)
	public void testMultipleClocks() throws Exception {
		Thread.sleep(200);
	}
	
}
