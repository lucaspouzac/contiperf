package org.databene.contiperf.junit;

import java.util.Random;

import org.databene.contiperf.Required;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Unrepeatable;
import org.databene.contiperf.report.EmptyReportModule;
import org.junit.Rule;
import org.junit.Test;

public class SmokeTest {
	
	@Rule public ContiPerfRule rule = new ContiPerfRule(new EmptyReportModule());
	
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
	@Required(max = 250, percentiles = "90:210,95:220")
	public void complexTest() throws Exception {
		Thread.sleep(200);
	}

	
	Random random = new Random();
	
	@Test
	@PerfTest(invocations = 1000, threads = 2)
	public void threadTest() throws Exception {
		Thread.sleep(random.nextInt(20));
	}

}
