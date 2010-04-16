/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

import org.databene.contiperf.util.InvokerProxy;
import org.databene.stat.LatencyCounter;

/**
 * {@link InvokerProxy} that provides performance tracking features.<br/><br/>
 * Created: 22.10.2009 16:36:43
 * @since 1.0
 * @author Volker Bergmann
 */
public class PerformanceTracker extends InvokerProxy {
	
    private PerformanceRequirement requirement;
    private boolean cancelOnViolation;
    private ExecutionLogger logger;
    private LatencyCounter counter;
    private boolean started;

	public PerformanceTracker(Invoker target, PerformanceRequirement requirement, boolean cancelOnViolation, ExecutionLogger logger) {
	    super(target);
	    this.requirement = requirement;
	    setExecutionLogger(logger);
	    this.started = false;
	    this.cancelOnViolation = cancelOnViolation;
    }

	public void setExecutionLogger(ExecutionLogger logger) {
		this.logger = logger;
	}
	
    public LatencyCounter getCounter() {
	    return counter;
    }

	public void start() {
    	int max = (requirement != null ? requirement.getMax() : -1);
    	counter = new LatencyCounter(max >= 0 ? max : 1000);
    	counter.start();
    	started = true;
	}
	
	@Override
    public Object invoke(Object[] args) throws Exception {
		if (!started)
			start();
	    long callStart = System.currentTimeMillis();
		Object result = super.invoke(args);
	    int latency = (int) (System.currentTimeMillis() - callStart);
	    counter.addSample(latency);
	    logger.logInvocation(getId(), latency, callStart);
	    if (requirement != null && requirement.getMax() >= 0 && latency > requirement.getMax() && cancelOnViolation)
	    	throw new PerfTestException("Method " + getId() + " exceeded time limit of " + 
	    			requirement.getMax() + " ms running " + latency + " ms");
	    return result;
	}
	
	public void stop() {
    	counter.stop();
    	counter.printSummary(new PrintWriter(System.out));
    	long elapsedTime = counter.duration();
    	logger.logSummary(getId(), elapsedTime, counter.sampleCount(), counter.getStartTime());
    	if (requirement != null)
    		checkRequirements(elapsedTime);
	}

	private void checkRequirements(long elapsedMillis) throws AssertionError {
	    long requiredTotalTime = requirement.getTotalTime();
    	if (requiredTotalTime >= 0) {
    		if (elapsedMillis > requiredTotalTime)
    			throw new AssertionError("Test run " + getId() + " exceeded timeout of " + 
    				requiredTotalTime + " ms running " + elapsedMillis + " ms");
    	}
    	int requiredThroughput = requirement.getThroughput();
    	if (requiredThroughput > 0) {
    		long actualThroughput = counter.sampleCount() * 1000 / elapsedMillis;
    		if (actualThroughput < requiredThroughput)
    			throw new AssertionError("Test " + getId() + " had a throughput of only " + 
        				actualThroughput + " calls per second, required: " + requiredThroughput + " calls per second");
    	}
    	int requiredAverage = requirement.getAverage();
		if (requiredAverage >= 0 && counter.averageLatency() > requiredAverage)
			throw new AssertionError("Average execution time of " + getId() + " exceeded the requirement of " + 
					requiredAverage + " ms, measured " + counter.averageLatency() + " ms");
    	for (PercentileRequirement percentile : requirement.getPercentileRequirements()) {
    		long measuredLatency = counter.percentileLatency(percentile.getPercentage());
			if (measuredLatency > percentile.getMillis())
    			throw new AssertionError(percentile.getPercentage() + "-percentile of " + getId() + " exceeded the requirement of " + 
    					percentile.getMillis() + " ms, measured " + measuredLatency + " ms");
    	}
    }

}
