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

package org.databene.contiperf.junit;

import org.databene.contiperf.ArgumentsProvider;
import org.databene.contiperf.EmptyArgumentsProvider;
import org.databene.contiperf.ExecutionConfig;
import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.Invoker;
import org.databene.contiperf.ConcurrentRunner;
import org.databene.contiperf.PerfTestException;
import org.databene.contiperf.PerformanceTracker;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.contiperf.CountRunner;
import org.databene.contiperf.TimedRunner;
import org.junit.runners.model.Statement;

/**
 * Implementation of {@link org.junit.runners.model.Statement} which wraps another Statement 
 * and adds multiple invocation, execution timing and duration check.<br/><br/>
 * Created: 12.10.2009 07:37:47
 * @since 1.0
 * @author Volker Bergmann
 */
final class PerfTestStatement extends Statement {
	
    private String id;
    private final Statement base;
    private ExecutionLogger logger;
    private ExecutionConfig config;
    private PerformanceRequirement requirement;

    PerfTestStatement(Statement base, String id, ExecutionConfig config, 
    		PerformanceRequirement requirement, ExecutionLogger logger) {
	    this.base = base;
	    this.id = id;
	    this.config = config;
	    this.requirement = requirement;
	    this.logger = logger;
    }

    @Override
    public void evaluate() throws Throwable {
    	Invoker invoker = new JUnitInvoker(id, base);
    	PerformanceTracker tracker = new PerformanceTracker(
    			invoker, requirement, config.isCancelOnViolation(), logger);
    	Runnable runner = createRunner(tracker);
		runner.run();
		tracker.stop();
    }

    private Runnable createRunner(PerformanceTracker tracker) {
	    ArgumentsProvider provider = new EmptyArgumentsProvider();
	    Runnable runner;
        int threads = config.getThreads();
		int duration = config.getDuration();
		int invocations = config.getInvocations();
		if (duration > 0) {
			if (threads == 1) {
				// multi-threaded timed test
				runner = new TimedRunner(tracker, provider, duration);
			} else {
				// single-threaded timed test
				Runnable[] runners = new Runnable[threads];
				for (int i = 0; i < threads; i++)
					runners[i] = new TimedRunner(tracker, provider, duration);
				runner = new ConcurrentRunner(id, runners);
			}
    	} else if (invocations >= 0) {
    		if (threads == 1) {
    			// single-threaded count-based test
    			runner = new CountRunner(tracker, provider, invocations);
    		} else {
    			// multi-threaded count-based test
    			Runnable[] runners = new Runnable[threads];
	        	int invocationsPerLoop = invocations / threads;
	        	int longerLoops = invocations - invocationsPerLoop * threads;
	        	for (int i = 0; i < threads; i++) {
	        		int loopSize = (i < longerLoops ? invocationsPerLoop + 1 : invocationsPerLoop);
	        		runners[i] = new CountRunner(tracker, provider, loopSize);
	        	}
				runner = new ConcurrentRunner(id, runners);
    		}
        } else 
        	throw new PerfTestException("No useful invocation count or duration defined");
	    return runner;
    }
    
}