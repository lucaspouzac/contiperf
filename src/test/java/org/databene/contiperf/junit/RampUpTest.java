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
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

/**
 * TODO Document class.<br/><br/>
 * Created: 06.04.2012 16:27:37
 * @since TODO version
 * @author Volker Bergmann
 */
public class RampUpTest {

	private long firstInvocationMillis = -1;
	private long lastInvocationMillis = -1;
	
	@Rule public ContiPerfRule rule = new ContiPerfRule();
	
	@After
	public void after() {
		String message = "expected an accumulated ramp-up and execution time of at least 980 ms, " +
				"but measured " + (lastInvocationMillis - firstInvocationMillis) + " ms";
		assertTrue(message, lastInvocationMillis - firstInvocationMillis > 980);
	}
	
	@Test
	@PerfTest(invocations = 3, threads = 3, rampUp = 500)
	public void testRampUp() throws InterruptedException {
		long currentTimeMillis = System.currentTimeMillis();
		if (firstInvocationMillis == -1)
			firstInvocationMillis = currentTimeMillis;
		lastInvocationMillis = currentTimeMillis;
		System.out.println("testRampUp()");
		Thread.sleep(2000);
	}

}
