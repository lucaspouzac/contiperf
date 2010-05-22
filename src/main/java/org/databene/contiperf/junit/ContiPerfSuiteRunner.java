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

package org.databene.contiperf.junit;

import java.util.Arrays;
import java.util.List;

import org.databene.contiperf.log.FileExecutionLogger;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * JUnit {@link Runner} class for wrapping test classes that are unaware of ContiPerf with 
 * a suite class that adds performance test and requirements configuration.<br/><br/>
 * Created: 02.05.2010 07:32:02
 * @since 1.05
 * @author Volker Bergmann
 */
public class ContiPerfSuiteRunner extends Suite {

	public ContiPerfSuiteRunner(Class<?> testClass) throws InitializationError {
	    super(testClass, new ContiPerfRunnerBuilder(testClass));
    }

	@Override
    public void run(RunNotifier runnotifier) {
	    super.run(runnotifier);
    }
	
	static class ContiPerfRunnerBuilder extends AllDefaultPossibilitiesBuilder {
		
		ContiPerfRule contiPerfRule;
		
		public ContiPerfRunnerBuilder(Class<?> suiteClass) {
			super(true);
			this.contiPerfRule = new ContiPerfRule(suiteClass, new FileExecutionLogger()); // TODO configure ExecutionLogger
        }

		@Override
        public Runner runnerForClass(Class<?> testClass) throws Throwable {
			List<RunnerBuilder> builders = Arrays.asList(
					ignoredBuilder(),
					annotatedBuilder(),
					suiteMethodBuilder(),
					junit3Builder(),
					contiPerfSuiteBuilder() // extends and replaces the JUnit4 builder
				);

			for (RunnerBuilder each : builders) {
				Runner runner = each.safeRunnerForClass(testClass);
				if (runner != null)
					return runner;
			}
			return null;
        }

		private RunnerBuilder contiPerfSuiteBuilder() {
	        return new RunnerBuilder() {

				@Override
                public Runner runnerForClass(Class<?> testClass) throws Throwable {
	                return new BlockContiPerfClassRunner(testClass, contiPerfRule);
                }
	        	
	        };
        }
		
	}
	
}
