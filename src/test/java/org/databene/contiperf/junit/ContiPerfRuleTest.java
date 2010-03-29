/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.log.ListExecutionLogger;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Tests the {@link ContiPerfRule}.<br/><br/>
 * Created: 29.03.2010 12:35:53
 * @since 1.0
 * @author Volker Bergmann
 */
public class ContiPerfRuleTest {

	@Test
	public void testDefault() throws Throwable {
		TestBean target = check("plain");
		assertEquals(1, target.plainCount.get());
	}

	@Test
	public void testInvocationCount() throws Throwable {
		TestBean target = check("five");
		assertEquals(5, target.fiveCount.get());
	}

	@Test
	public void testDuration() throws Throwable {
		TestBean target = check("duration1000");
		int count = target.duration1000Count.get();
		assertTrue("Expected at least 7 invocations, but had only " + count, count > 7);
	}

	@Test
	public void testMedianSuccessful() throws Throwable {
		check("median100Successful");
	}

	@Test(expected = AssertionError.class)
	public void testMedianFailed() throws Throwable {
		check("median1Failed");
	}

	@Test
	public void testAverageSuccessful() throws Throwable {
		check("average100Successful");
	}

	@Test(expected = AssertionError.class)
	public void testAverageFailed() throws Throwable {
		check("average1Failed");
	}

	@Test
	public void testMaxSuccessful() throws Throwable {
		check("max100Successful");
	}

	@Test(expected = AssertionError.class)
	public void testMaxFailed() throws Throwable {
		check("max1Failed");
	}
	
	@Test
	public void testThroughputSuccessful() throws Throwable {
		check("throughputSuccessful");
	}
	
	@Test(expected = AssertionError.class)
	public void testThroughputFailed() throws Throwable {
		check("throughputFailed");
	}
	
	@Test
	public void testTotalTimeSuccessful() throws Throwable {
		check("totalTimeSuccessful");
	}
	
	@Test(expected = AssertionError.class)
	public void testTotalTimeFailed() throws Throwable {
		check("totalTimeFailed");
	}
	
	@Test
	public void testPercentileSuccessful() throws Throwable {
		check("percentileSuccessful");
	}
	
	@Test(expected = AssertionError.class)
	public void testPercentileFailed() throws Throwable {
		check("percentileFailed");
	}
	
	

	private TestBean check(String methodName) throws NoSuchMethodException, Throwable {
	    ContiPerfRule rule = new ContiPerfRule(new ListExecutionLogger());
		TestBean target = new TestBean();
		Method method = TestBean.class.getDeclaredMethod(methodName, new Class<?>[0]);
		Statement base = new InvokerStatement(target, method);
		FrameworkMethod fwMethod = new FrameworkMethod(method);
		Statement perfTestStatement = rule.apply(base, fwMethod, target);
		perfTestStatement.evaluate();
	    return target;
    }
	
	
	
	public static class TestBean {
		
		public AtomicInteger plainCount = new AtomicInteger();
		
		public void plain() {
			plainCount.incrementAndGet();
		}
		
		public AtomicInteger fiveCount = new AtomicInteger();
		
		@PerfTest(invocations = 5)
		public void five() {
			fiveCount.incrementAndGet();
		}
		
		public AtomicInteger duration1000Count = new AtomicInteger();
		@PerfTest(duration = 1000)
		public void duration1000() throws Exception {
			Thread.sleep(100);
			duration1000Count.incrementAndGet();
		}

		@PerfTest(invocations = 5)
		@Required(median = 100)
		public void median100Successful() {
		}

		@PerfTest(invocations = 5)
		@Required(median = 1)
		public void median1Failed() throws InterruptedException {
			Thread.sleep(10);
		}

		@PerfTest(invocations = 5)
		@Required(average = 100)
		public void average100Successful() {
		}

		@PerfTest(invocations = 5)
		@Required(average = 1)
		public void average1Failed() throws InterruptedException {
			Thread.sleep(10);
		}
		
		@Required(max = 100)
		public void max100Successful() {
		}

		@Required(max = 1)
		public void max1Failed() throws InterruptedException {
			Thread.sleep(10);
		}
		
		@PerfTest(invocations = 10)
		@Required(throughput = 10)
		public void throughputSuccessful() throws InterruptedException {
			Thread.sleep(10);
		}
		
		@PerfTest(invocations = 10)
		@Required(throughput = 150)
		public void throughputFailed() throws InterruptedException {
			Thread.sleep(10);
		}
		
		@PerfTest(invocations = 10)
		@Required(totalTime = 500)
		public void totalTimeSuccessful() throws InterruptedException {
			Thread.sleep(10);
		}
		
		@PerfTest(invocations = 10)
		@Required(totalTime = 50)
		public void totalTimeFailed() throws InterruptedException {
			Thread.sleep(10);
		}
		
		@PerfTest(invocations = 10)
		@Required(percentiles = "90:50")
		public void percentileSuccessful() throws InterruptedException {
			Thread.sleep(10);
		}
		
		@PerfTest(invocations = 10)
		@Required(percentiles = "90:5")
		public void percentileFailed() throws InterruptedException {
			Thread.sleep(10);
		}
		
	}
	
	
	
	public static class InvokerStatement extends Statement {
		
		public TestBean target;
		public Method method;
		
		public InvokerStatement(TestBean target, Method method) {
	        this.target = target;
	        this.method = method;
        }

		@Override
        public void evaluate() throws Throwable {
	        method.invoke(target);
        }
	}

}
