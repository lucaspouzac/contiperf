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

import java.lang.reflect.Constructor;

import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Parent class for tests that verify ContiPerf functionality.<br/><br/>
 * Created: 24.05.2010 06:18:21
 * @since 1.05
 * @author Volker Bergmann
 */
public abstract class AbstractContiPerfTest {

	protected boolean finished;
	protected boolean failed;
	protected boolean assumptionFailed;
	protected boolean ignored;
	
	@Before
	public void setUp() {
		finished = false;
		failed = false;
		assumptionFailed = false;
		ignored = false;
	}

	protected void runTest(Class<?> testClass) throws Exception {
		RunWith runWith = testClass.getAnnotation(RunWith.class);
		if (runWith != null)
			runAnnotatedTestClass(testClass, runWith);
		else
			runPlainTestClass(testClass);
	}
	
	private void runPlainTestClass(Class<?> testClass) throws Exception {
    	BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(testClass);
    	RunNotifier notifier = new RunNotifier();
    	notifier.addListener(new MyListener());
    	runner.run(notifier);
    }
    
    private void runAnnotatedTestClass(Class<?> testClass, RunWith runWith) throws Exception {
    	Class<? extends Runner> runnerClass = runWith.value();
    	Constructor<? extends Runner> constructor = runnerClass.getConstructor(Class.class);
    	Runner runner = constructor.newInstance(testClass);
    	RunNotifier notifier = new RunNotifier();
    	notifier.addListener(new MyListener());
    	runner.run(notifier);
    }
    
    protected class MyListener extends RunListener {
    	
    	
    	@Override
        public void testFinished(Description description) throws Exception {
    		finished = true;
    	}

    	@Override
        public void testFailure(Failure failure) throws Exception {
    		failed = true;
    	}

    	@Override
        public void testAssumptionFailure(Failure failure) {
    		assumptionFailed = true;
    	}

    	@Override
        public void testIgnored(Description description) throws Exception {
    		ignored = true;
    	}
    }

}
