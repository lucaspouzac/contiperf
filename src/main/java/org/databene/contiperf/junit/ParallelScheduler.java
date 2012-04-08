/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.runners.model.RunnerScheduler;

/**
 * {@link RunnerScheduler} which executes all tests in parallel.<br/><br/>
 * Created: 08.04.2012 07:11:38
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class ParallelScheduler implements RunnerScheduler {
	
	private Queue<Future<String>> tasks = new LinkedList<Future<String>>();
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private CompletionService<String> completionService = new ExecutorCompletionService<String>(executorService);

	public void schedule(final Runnable childStatement) {
		Future<String> future = completionService.submit(new Callable<String>() {
			public String call() {
				childStatement.run();
				return toString();
			}
			
			@Override
			public String toString() {
				return childStatement.toString();
			}
		});
		tasks.add(future);
	}

	public void finished() {
		try {
			while (!tasks.isEmpty()) {
				Future<String> task = completionService.take();
				//System.out.println("Completed " + task.get());
				tasks.remove(task);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			while (!tasks.isEmpty())
				tasks.poll().cancel(true);
			executorService.shutdownNow();
		}
	}
	
}
