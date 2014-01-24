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

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.databene.contiperf.util.ContiPerfUtil;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Executes all tests of one test class concurrently. It's possible to specify
 * thread number with {@link Parallel} annotation.<br/>
 * <br/>
 * Created: 07.04.2012 17:18:54
 * 
 * @See {@link Parallel}
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class ParallelRunner extends BlockJUnit4ClassRunner {

    public ParallelRunner(Class<?> type) throws InitializationError {
	super(type);
	setScheduler(new ParallelScheduler(createExecutor(type)));
    }

    private static ExecutorService createExecutor(Class<?> type) {
	Parallel parallel = ContiPerfUtil.annotationOfClass(type,
		Parallel.class);
	if (parallel != null) {
	    return newFixedThreadPool(parallel.count(),
		    new ConcurrentTestRunnerThreadFactory());
	}
	return newCachedThreadPool(new ConcurrentTestRunnerThreadFactory());
    }

    private static class ConcurrentTestRunnerThreadFactory implements
	    ThreadFactory {
	private AtomicLong count = new AtomicLong();

	public Thread newThread(Runnable runnable) {
	    return new Thread(runnable, ParallelRunner.class.getSimpleName()
		    + "-Thread-" + count.getAndIncrement());
	}
    }

}
