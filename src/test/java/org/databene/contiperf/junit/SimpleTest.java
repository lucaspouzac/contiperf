package org.databene.contiperf.junit;

import org.databene.contiperf.PerfTest;
import org.junit.Rule;
import org.junit.Test;

public class SimpleTest {
	
	@Rule
	public ContiPerfRule i = new ContiPerfRule();
	
	@Test
	public void sleepAWhile() throws Exception {
		Thread.sleep(100);
	}

	@PerfTest(invocations = 5, timeLimit = 1200)
	@Test(timeout = 250)
	public void sleepALittleLonger() throws Exception {
		Thread.sleep(200);
	}

}
