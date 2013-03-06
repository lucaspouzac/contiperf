/*
 * (c) Copyright 2009-2013 by Volker Bergmann. All rights reserved.
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

package org.databene.contiperf;

import java.io.PrintWriter;

import org.databene.contiperf.clock.SystemClock;
import org.databene.contiperf.report.ReportContext;
import org.databene.contiperf.report.ReportModule;
import org.databene.contiperf.util.InvokerProxy;
import org.databene.stat.LatencyCounter;

/**
 * {@link InvokerProxy} that provides performance tracking features.<br/><br/>
 * Created: 22.10.2009 16:36:43
 * @since 1.0
 * @author Volker Bergmann
 */
public class PerformanceTracker extends InvokerProxy {
	
	private final ExecutionConfig executionConfig;
    private final PerformanceRequirement requirement;
    
    private ReportContext context;
    
    private Clock[] clocks;
    private LatencyCounter[] counters;
    private boolean trackingStarted;
    private long warmUpFinishedTime;

	public PerformanceTracker(Invoker target, 
			PerformanceRequirement requirement, ReportContext context) {
	    this(target, null, requirement, context, new Clock[] { new SystemClock() });
    }

	public PerformanceTracker(Invoker target, ExecutionConfig executionConfig, 
			PerformanceRequirement requirement, ReportContext context, Clock[] clocks) {
	    super(target);
	    this.executionConfig = (executionConfig != null ? executionConfig : new ExecutionConfig(0));
	    this.requirement = requirement;
	    this.setContext(context);
	    this.clocks = clocks;
	    this.counters = null;
	    this.trackingStarted = false;
	    this.warmUpFinishedTime = -1;
    }
	
	public void setContext(ReportContext context) {
		this.context = context;
	}
	
    public LatencyCounter[] getCounters() {
	    return counters;
    }

	public void startTracking() {
		reportStart();
    	int max = (requirement != null ? requirement.getMax() : -1);
    	this.counters = new LatencyCounter[clocks.length];
    	for (int i = 0; i < clocks.length; i++) {
        	LatencyCounter counter = new LatencyCounter(target.toString(), clocks[i].getName(), max >= 0 ? max : 1000);
        	this.counters[i] = counter;
    		counter.start();
    	}
    	trackingStarted = true;
	}
	
	@Override
    public Object invoke(Object[] args) throws Exception {
	    long clock0StartTime = clocks[0].getTime();
    	long realStartMillis = System.nanoTime() / 1000000;
		if (warmUpFinishedTime == -1)
			warmUpFinishedTime = realStartMillis + executionConfig.getWarmUp();
	    checkState(realStartMillis);
		Object result = super.invoke(args);
	    int latency = (int) (clocks[0].getTime() - clock0StartTime);
	    if (isTrackingStarted())
	    	for (LatencyCounter counter : counters)
	    		counter.addSample(latency);
	    reportInvocation(latency, realStartMillis);
	    if (requirement != null && requirement.getMax() >= 0 && latency > requirement.getMax() 
	    		&& executionConfig.isCancelOnViolation())
	    	context.fail("Method " + getId() + " exceeded time limit of " + 
	    			requirement.getMax() + " ms running " + latency + " ms");
	    return result;
	}

	private synchronized void checkState(long callStart) {
		if (callStart >= warmUpFinishedTime && !trackingStarted)
			startTracking();
	}
	
	public boolean isTrackingStarted() {
		return trackingStarted;
	}
	
	public void stopTracking() {
		if (!isTrackingStarted())
			throw new RuntimeException("Trying to stop counter before it was started");
		for (LatencyCounter counter : counters)
			counter.stop();
    	LatencyCounter mainCounter = counters[0];
		mainCounter.printSummary(new PrintWriter(System.out));
    	reportCompletion();
    	if (requirement != null)
    		checkRequirements(mainCounter.duration());
    	this.trackingStarted = false;
	}

	public void clear() {
		counters = null;
	}

	// helper methods --------------------------------------------------------------------------------------------------
	
	private void reportStart() {
		for (ReportModule module : context.getReportModules())
			module.starting(getId());
	}

	private void reportInvocation(int latency, long callStart) {
		for (ReportModule module : context.getReportModules())
			module.invoked(getId(), latency, callStart);
	}

	private void reportCompletion() {
		for (ReportModule module : context.getReportModules())
			module.completed(getId(), counters, executionConfig, requirement);
	}

	private void checkRequirements(long elapsedMillis) {
	    long requiredMax = requirement.getMax();
    	LatencyCounter mainCounter = counters[0];
		if (requiredMax >= 0) {
    		if (mainCounter .maxLatency() > requiredMax)
    			context.fail("The maximum latency of " + 
    					requiredMax + " ms was exceeded, Measured: " + mainCounter.maxLatency() + " ms");
    	}
	    long requiredTotalTime = requirement.getTotalTime();
    	if (requiredTotalTime >= 0) {
    		if (elapsedMillis > requiredTotalTime)
    			context.fail("Test run " + getId() + " exceeded timeout of " + 
    				requiredTotalTime + " ms running " + elapsedMillis + " ms");
    	}
    	int requiredThroughput = requirement.getThroughput();
    	if (requiredThroughput > 0 && elapsedMillis > 0) {
    		long actualThroughput = mainCounter.sampleCount() * 1000 / elapsedMillis;
    		if (actualThroughput < requiredThroughput)
    			context.fail("Test " + getId() + " had a throughput of only " + 
        				actualThroughput + " calls per second, required: " + requiredThroughput + " calls per second");
    	}
    	int requiredAverage = requirement.getAverage();
		if (requiredAverage >= 0 && mainCounter.averageLatency() > requiredAverage)
			context.fail("Average execution time of " + getId() + " exceeded the requirement of " + 
					requiredAverage + " ms, measured " + mainCounter.averageLatency() + " ms");
    	for (PercentileRequirement percentile : requirement.getPercentileRequirements()) {
    		long measuredLatency = mainCounter.percentileLatency(percentile.getPercentage());
			if (measuredLatency > percentile.getMillis())
				context.fail(percentile.getPercentage() + "-percentile of " + getId() + " exceeded the requirement of " + 
    					percentile.getMillis() + " ms, measured " + measuredLatency + " ms");
    	}
    }

}
