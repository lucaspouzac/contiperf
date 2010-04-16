/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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
 * Runs several {@link Runnable}s concurrently. 
 * If a {@link Throwable} is encountered, execution of all threads is canceled.<br/><br/>
 * Created: 15.04.2010 23:42:30
 * @since 1.03
 * @author Volker Bergmann
 */
public class ConcurrentRunner implements Runnable {

	private String name;
	private Runnable[] runners;
	
	public ConcurrentRunner(String name, Runnable[] runners) {
	    this.name = name;
	    this.runners = runners;
    }

    public void run() {
		CPThreadGroup threadGroup = new CPThreadGroup(name);
	    Thread[] threads = new Thread[runners.length];
	    for (int i = 0; i < runners.length; i++)
	        threads[i] = new Thread(threadGroup, runners[i]);
	    for (Thread thread : threads)
	    	thread.start();
	    try {
	        for (Thread thread : threads)
	        	thread.join();
        } catch (InterruptedException e) {
        	// if the thread group has an exception, that one is more interesting
        	if (threadGroup.throwable == null)
        		throw new PerfTestException(e); // interruption without throwable cause
        }
        // The thread group encountered a Throwable, report it to the caller
    	if (threadGroup.throwable != null)
    		throw ContiPerfUtil.runtimeCause(threadGroup.throwable);
    }

    /** 
     * Implements the {@link ThreadGroup#uncaughtException(Thread, Throwable)} method
     * interrupting the execution of all threads in case of a {@link Throwable} and
     * memorizing the {@link Throwable}.
     */
    class CPThreadGroup extends ThreadGroup {
    	
    	Throwable throwable;

		public CPThreadGroup(String name) {
	        super(name);
        }
    	
		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
		    if (this.throwable == null)
		    	this.throwable = throwable;
		    interrupt();
		}
    }
    
}
