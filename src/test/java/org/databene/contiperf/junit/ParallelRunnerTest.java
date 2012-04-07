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

import org.databene.contiperf.ParallelRunner;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.timer.ConstantTimer;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * TODO Document class.<br/><br/>
 * Created: 07.04.2012 17:36:35
 * @since 2.1.0
 * @author Volker Bergmann
 */
@RunWith(ParallelRunner.class)
public class ParallelRunnerTest {
	
	@Rule public ContiPerfRule rule = new ContiPerfRule();
	
	private static volatile long test1First = -1;
	private static volatile long test1Last = -1;
	
	private static volatile long test2First = -1;
	private static volatile long test2Last = -1;
	
	@Test
	@PerfTest(duration = 2000, threads = 2, timer = ConstantTimer.class, timerParams = { 1200 })
	public void test1() throws Exception {
		long currentTime = System.currentTimeMillis();
		if (test1First == -1)
			test1First = currentTime;
		test1Last = currentTime;
		System.out.println("test1 " + (currentTime - Math.min(test1First, test2First)));
	}
	
	@Test
	@PerfTest(duration = 3000, threads = 2, timer = ConstantTimer.class, timerParams = { 700 })
	public void test2() throws Exception {
		long currentTime = System.currentTimeMillis();
		if (test2First == -1)
			test2First = currentTime;
		test2Last = currentTime;
		System.out.println("test2 " + (currentTime - Math.min(test1First, test2First)));
	}
	
	@Test
	public void test3() throws Exception {
	}
	
	@AfterClass
	public static void verifyParallelExecution() {
		assertTrue(
			(test1First <= test2First && test2First <= test1Last) ||
			(test2First <= test1First && test1First <= test2Last)
		);
	}
	
}
