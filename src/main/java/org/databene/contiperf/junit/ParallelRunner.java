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

import java.lang.reflect.Method;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;

/**
 * Executes all tests of one test class concurrently. 
 * Warning: This is an experimental implementation, so its behaviour may change in future versions.<br/><br/>
 * Created: 07.04.2012 17:18:54
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class ParallelRunner extends BlockJUnit4ClassRunner {

	public ParallelRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	protected Statement childrenInvoker(final RunNotifier notifier) {
		return new Statement() {
			@Override
			public void evaluate() {
				runChildren(notifier);
			}
		};
	}

	private void runChildren(final RunNotifier notifier) {
		RunnerScheduler scheduler = new ParallelScheduler();
		for (FrameworkMethod method : getChildren())
 			scheduler.schedule(new ChildRunnable(method, notifier));
		scheduler.finished();
	}

	public class ChildRunnable implements Runnable {

		FrameworkMethod method;
		RunNotifier notifier;
		
		public ChildRunnable(FrameworkMethod method, RunNotifier notifier) {
			this.method = method;
			this.notifier = notifier;
		}

		public void run() {
			ParallelRunner.this.runChild(method, notifier);
		}
		
		@Override
		public String toString() {
			Method realMethod = method.getMethod();
			return realMethod.getDeclaringClass().getSimpleName() + '.' + realMethod.getName() + "()";
		}
	}

}
