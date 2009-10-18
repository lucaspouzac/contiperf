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

package org.databene.contiperf.junit;

import org.databene.contiperf.ExecutionConfig;
import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.stat.LatencyCounter;
import org.junit.Assert;
import org.junit.runners.model.Statement;

/**
 * {@link Statement} implementation that wraps another Statement and adds
 * multiple invocation, execution timing and duration check.<br/><br/>
 * Created: 12.10.2009 07:37:47
 * @since 1.0
 * @author Volker Bergmann
 */
final class MultiCallStatement extends Statement {
	
    private final Statement base;
    private String id;
    private ExecutionLogger logger;
    private ExecutionConfig config;
    private PerformanceRequirement requirement;

    MultiCallStatement(Statement base, String id, ExecutionConfig config, 
    		PerformanceRequirement requirement, ExecutionLogger logger) {
	    this.base = base;
	    this.id = id;
	    this.config = config;
	    this.requirement = requirement;
	    this.logger = logger;
    }

    @Override
    public void evaluate() throws Throwable {
    	int max = (requirement != null ? requirement.getMax() : -1);
    	int maxCounter = (max >= 0 ? max : 1000);
    	LatencyCounter counter = new LatencyCounter(maxCounter);
    	long startTime = System.nanoTime();
    	for (int i = 0; i < config.getInvocations(); i++) {
        	int latency = measureSingleExecution();
			if (max >= 0 && latency > max)
        		Assert.fail("Method " + id + " exceeded time limit of " + 
        				max + "ms running " + latency + " ms");
        	counter.addSample(latency);
    	}
    	long elapsedTime = System.nanoTime() - startTime;
    	logger.logSummary(id, elapsedTime, config.getInvocations(), startTime);
    	long totalTime = config.getTimeout();
    	if (totalTime >= 0) {
    		int elapsedMillis = (int) (elapsedTime / 1000000);
    		if (elapsedMillis > totalTime)
    		Assert.fail("Test run " + id + " exceeded timeout of " + 
    				totalTime + " ms running " + elapsedMillis + " ms");
    	}
    }

	private int measureSingleExecution() throws Throwable {
	    long callStart = System.nanoTime();
	    base.evaluate();
	    return (int) ((System.nanoTime() - callStart) / 1000000);
    }
    
}