/*
 * (c) Copyright 2012-2013 by Volker Bergmann. All rights reserved.
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

import java.util.List;

import org.databene.contiperf.Clock;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.clock.AbstractClock;
import org.databene.contiperf.report.InvocationLog;
import org.databene.contiperf.report.ListReportModule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests the usage of custom {@link Clock}s.<br/><br/>
 * Created: 24.05.2012 07:36:24
 * @since 2.2.0
 * @author Volker Bergmann
 */
public class CustomClockTest {
	
	@Rule public ContiPerfRule rule = new ContiPerfRule(new ListReportModule());
	
	@Test
	@PerfTest(invocations = 10, clocks = { ConstantClock.class })
	public void test() throws InterruptedException {
		Thread.sleep(50);
	}
	
	public static class ConstantClock extends AbstractClock {
		public ConstantClock() {
			super("constantClock");
		}

		public long getTime() {
			return 35;
		}
	}
	
	@After
	public void verify() {
		ListReportModule report = rule.getContext().getReportModule(ListReportModule.class);
		List<InvocationLog> invocations = report.getInvocations();
		assertEquals(10, invocations.size());
		for (InvocationLog log : invocations)
			assertEquals( 0, log.latency);
	}
	
}
