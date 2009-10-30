package org.databene.contiperf.junit;

import org.databene.contiperf.Percentile;
import org.databene.contiperf.Required;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Unrepeatable;
import org.junit.Rule;
import org.junit.Test;

public class SimpleTest {
	
	@Rule
	public ContiPerfRule i = new ContiPerfRule();

	
	@Test
	public void simpleTest() throws Exception {
		Thread.sleep(100);
	}

	
	@Unrepeatable @Test
	public void unrepeatableTest() throws Exception {
		Thread.sleep(100);
	}

	
	@Test(timeout = 250)
	@PerfTest(invocations = 5)
	@Required(max = 1200, percentile90 = 220)
	public void detailedTest() throws Exception {
		Thread.sleep(200);
	}

	@Test(timeout = 250)
	@PerfTest(duration = 2000)
	@Required(throughput = 4)
	public void continuousTest() throws Exception {
		Thread.sleep(200);
	}


	@Test(timeout = 300)
	@PerfTest(invocations = 5)
	@Required(max = 250, 
		percentiles = {
			@Percentile(base = 90, limit=210),
			@Percentile(base = 95, limit=220)
		})
	public void complexTest() throws Exception {
		Thread.sleep(200);
	}

}
