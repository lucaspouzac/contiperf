package org.databene.contiperf.junit;

import org.junit.Rule;
import org.junit.Test;

public class SimpleTest {
	
	@Rule
	public ContiPerfRule i = new ContiPerfRule();
	
	@Test
	public void sleepASecond() throws Exception {
		Thread.sleep(1000);
	}

}
