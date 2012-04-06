/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

import java.util.concurrent.atomic.AtomicLong;

import org.databene.contiperf.ArgumentsProvider;
import org.databene.contiperf.EmptyArgumentsProvider;
import org.databene.contiperf.ExecutionConfig;
import org.databene.contiperf.InvocationRunner;
import org.databene.contiperf.Invoker;
import org.databene.contiperf.ConcurrentRunner;
import org.databene.contiperf.PerfTestConfigurationError;
import org.databene.contiperf.PerformanceTracker;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.contiperf.CountRunner;
import org.databene.contiperf.TimedRunner;
import org.databene.contiperf.WaitTimer;
import org.databene.contiperf.report.ReportContext;
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
    private ReportContext context;
    private ExecutionConfig config;
    private PerformanceRequirement requirement;

    PerfTestStatement(Statement base, String id, ExecutionConfig config, 
    		PerformanceRequirement requirement, ReportContext context) {
	    this.base = base;
	    this.id = id;
	    this.config = config;
	    this.requirement = requirement;
	    this.context = context;
    }

    @Override
    public void evaluate() throws Throwable {
		System.out.println(id);
    	Invoker invoker = new JUnitInvoker(id, base);
    	PerformanceTracker tracker = new PerformanceTracker(
    			invoker, requirement, config.isCancelOnViolation(), context);
    	InvocationRunner runner = createRunner(tracker);
    	try {
			runner.run();
    	} finally {
			tracker.stop();
    		runner.close();
    		tracker.clear();
    	}
    }

    private InvocationRunner createRunner(PerformanceTracker tracker) {
	    ArgumentsProvider provider = new EmptyArgumentsProvider();
	    InvocationRunner runner;
        int threads = config.getThreads();
		int duration = config.getDuration();
		int invocations = config.getInvocations();
		int rampUp = config.getRampUp();
		WaitTimer wait = config.getWaitTimer();
		if (duration > 0) {
			if (threads == 1) {
				// single-threaded timed test
				runner = new TimedRunner(tracker, provider, wait, duration);
			} else {
				// multi-threaded timed test
				InvocationRunner[] runners = new InvocationRunner[threads];
				for (int i = 0; i < threads; i++)
					runners[i] = new TimedRunner(tracker, provider, wait, duration);
				runner = new ConcurrentRunner(id, runners, rampUp);
			}
    	} else if (invocations >= 0) {
    		AtomicLong counter = new AtomicLong(invocations);
    		if (threads == 1) {
    			// single-threaded count-based test
    			runner = new CountRunner(tracker, provider, wait, counter);
    		} else {
    			// multi-threaded count-based test
    			InvocationRunner[] runners = new InvocationRunner[threads];
	        	for (int i = 0; i < threads; i++)
	        		runners[i] = new CountRunner(tracker, provider, wait, counter);
				runner = new ConcurrentRunner(id, runners, rampUp);
    		}
        } else 
        	throw new PerfTestConfigurationError("No useful invocation count or duration defined");
	    return runner;
    }
    
}