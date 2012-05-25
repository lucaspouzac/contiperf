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

import org.databene.contiperf.clock.SystemClock;
import org.databene.contiperf.timer.None;

/**
 * Holds the execution configuration for a single test.<br/><br/>
 * Created: 18.10.2009 06:31:25
 * @since 1.0
 * @author Volker Bergmann
 */
public class ExecutionConfig {
	
	private int invocations;
	private int duration;
	private Clock[] clocks;
	private int rampUp;
	private int warmUp;
	private int threads;
	WaitTimer waitTimer;
	private boolean cancelOnViolation;
	// TODO v2.x private int timeout;
	
	public ExecutionConfig(int invocations) {
	    this(invocations, 1, -1, new Clock[] { new SystemClock() }, 0, 0, false, None.class, new double[0] /*, -1*/);
    }

	public ExecutionConfig(int invocations, int threads, int duration, Clock[] clocks, int rampUp, int warmUp, boolean cancelOnViolation,
			Class<? extends WaitTimer> waitTimerClass, double[] waitParams /*, int timeout*/) {
	    this.invocations = invocations;
	    this.threads = threads;
	    this.duration = duration;
	    this.clocks = clocks;
	    this.rampUp = rampUp;
	    this.warmUp = warmUp;
	    this.cancelOnViolation = cancelOnViolation;
	    try {
			waitTimer = (WaitTimer) waitTimerClass.newInstance();
			waitTimer.init(waitParams);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	    //this.timeout = timeout;
    }

	public int getInvocations() {
		return invocations;
	}

	public void setInvocations(int invocations) {
    	this.invocations = invocations;
    }
	
	public int getThreads() {
		return threads;
	}

	public int getDuration() {
		return duration;
	}
	
	public int getRampUp() {
		return rampUp;
	}
	
	public int getWarmUp() {
		return warmUp;
	}
	
	public WaitTimer getWaitTimer() {
		return waitTimer;
	}
	
/* 
	public int getTimeout() {
		return timeout;
	}
*/
	public boolean isCancelOnViolation() {
		return cancelOnViolation;
	}

	@Override
	public String toString() {
	    return (invocations > 0 ? invocations + " invocations" : "Running" + duration + " ms") + 
	    	" with " + threads + " threads";
	}

	public Clock[] getClocks() {
		return clocks;
	}

}
