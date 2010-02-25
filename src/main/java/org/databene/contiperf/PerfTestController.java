/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import org.databene.stat.LatencyCounter;

/**
 * TODO Document class.<br/><br/>
 * Created: 22.10.2009 16:36:43
 * @since 1.0
 * @author Volker Bergmann
 */
public class PerfTestController {
	
	private Invoker invoker;
    private PerformanceRequirement requirement;
    private ExecutionLogger logger;
    private LatencyCounter counter;
    private boolean started;

	public PerfTestController(Invoker invoker, PerformanceRequirement requirement, ExecutionLogger logger) {
	    this.invoker = invoker;
	    this.requirement = requirement;
	    this.logger = logger;
	    this.started = false;
    }

	public void start() {
    	int max = (requirement != null ? requirement.getMax() : -1);
    	counter = new LatencyCounter(max >= 0 ? max : 1000);
    	counter.start();
    	started = true;
	}
	
	public void invoke(Object[] args) throws Exception {
		if (!started)
			start();
	    long callStart = System.currentTimeMillis();
		invoker.invoke(args);
	    int latency = (int) (System.currentTimeMillis() - callStart);
	    counter.addSample(latency);
	    logger.logInvocation(invoker.getId(), latency, callStart);
	    if (requirement != null && requirement.getMax() >= 0 && latency > requirement.getMax())
	    	throw new AssertionError("Method " + invoker.getId() + " exceeded time limit of " + 
	    			requirement.getMax() + " ms running " + latency + " ms");
	}
	
	public void stop() {
    	counter.stop();
    	long elapsedTime = counter.duration();
    	logger.logSummary(invoker.getId(), elapsedTime, counter.sampleCount(), counter.getStartTime());
    	long maxTotalTime = requirement.getTotalTime();
    	counter.printSummary(new PrintWriter(System.out));
    	if (maxTotalTime >= 0) {
    		int elapsedMillis = (int) (elapsedTime / 1000000);
    		if (elapsedMillis > maxTotalTime)
    			throw new AssertionError("Test run " + invoker.getId() + " exceeded timeout of " + 
    				maxTotalTime + " ms running " + elapsedMillis + " ms");
    	}
	}
	
}
