/*
 * (c) Copyright 2010-2012 by Volker Bergmann. All rights reserved.
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

import org.databene.contiperf.util.ContiPerfUtil;

/**
 * Calls the invoker for a certain amount of time.<br/><br/>
 * Created: 15.04.2010 23:13:52
 * @since 1.03
 * @author Volker Bergmann
 */
public class TimedRunner extends AbstractInvocationRunner {

    private long duration;
    private ArgumentsProvider argsProvider;
    private Invoker invoker;

    public TimedRunner(Invoker invoker, ArgumentsProvider argsProvider, 
    		WaitTimer waitTimer, long duration) {
    	super(waitTimer);
	    this.invoker = invoker;
	    this.argsProvider = argsProvider;
	    this.duration = duration;
    }

	public void run() {
		try {
		    long start = System.currentTimeMillis();
		    long endTime = start + duration;
		    do {
	    	    invoker.invoke(argsProvider.next());
	    	    sleep();
		    } while (System.currentTimeMillis() < endTime);
		} catch (Exception e) {
			throw ContiPerfUtil.executionError(e);
		}
    }

	public void close() {
	    invoker = null;
    }
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + duration + " ms)";
	}

}
