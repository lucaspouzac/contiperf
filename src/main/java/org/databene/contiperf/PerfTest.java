/*
 * (c) Copyright 2009-2012 by Volker Bergmann. All rights reserved.
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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.databene.contiperf.timer.ConstantTimer;
import org.databene.contiperf.timer.CumulatedTimer;
import org.databene.contiperf.timer.None;
import org.databene.contiperf.timer.RandomTimer;

/**
 * Defines execution details and performance requirements for a test method: How
 * long the test should take, the level of concurrency and timings to apply. Two
 * basic modes can be used: count-based or duration-based execution.
 * 
 * <h3>Count-based execution</h3> In count-based execution, the total number of
 * test executions is stated and ContiPerf runs until the number has been
 * reached - however slow or fast this may be. Example:
 * <code>@PerfTest(invocations = 3000, threads = 10)</code> advises ContiPerf to
 * execute the annotated test method 3000 times with up to 10 threads in
 * parallel
 * 
 * <h3>Duration-based execution</h3> In duration-based execution ContiPerf
 * repeats test invocation only as long as a stated test duration has been
 * reached or exceeded. This is especially suited for performing test runs over
 * a certain (longer) period of time for memory leak and stability testing.
 * 
 * <h3>Timing</h3> In order not to have tests run continuously at full speed,
 * but to achieve some more user-interaction-like behavior, timers can be used:
 * They incur a wait time between invocations. ContiPerf comes with some
 * predefined timers ({@link ConstantTimer}, {@link RandomTimer} and
 * {@link CumulatedTimer}) and you can easily define custom ones. Example:
 * <code>@PerfTest(invocations = 1000, threads = 10, timer = RandomTimer.class,
 * timerParams = { 30, 80 })</code> causes ContiPerf to wait for 30 to 80
 * milliseconds between the test invocations of each thread.
 * 
 * <h3>Ramp-up and warm-up time</h3> If the tested component or system would be
 * overloaded if all thread were immediately accessing it, a ramp-up mechanism
 * can be used: When specifying a {@link #rampUp()} time, the test run begins
 * with a single thread. After the ramp-up period, a second thread is added,
 * after one more ramp-up period a third and so on until the full number of
 * threads has been reached. In order to ease switching between ramp-up
 * scenarios, the {@link #duration()} always specifies the time running with
 * full number of threads, ramp-up times are always added to the duration.
 * Example:
 * <code>@PerfTest(threads = 10, duration = 60000, rampUp = 1000)</code> makes
 * ContiPerf start with one thread, add a new thread each second until 10
 * threads are reached (which is the case after 9 seconds) and then runs the
 * test at the full number of threads for 60 seconds. Consequentially, the total
 * amount of time of test runs is 69 seconds. For measuring only the
 * characteristics under full load, you can configure a {@link #warmUp()} time
 * to tell ContiPerf after which amount of time it should begin to measure and
 * validate test execution. For the example above, a minimum rampUp time of 9
 * seconds is useful:
 * <code>@PerfTest(threads = 10, duration = 60000, rampUp = 1000, warmUp = 9000)</code>
 * <br/>
 * <br/>
 * Created: 14.10.2009 14:41:18
 * 
 * @since 1.0
 * @author Volker Bergmann
 */
@Documented
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface PerfTest {

    /**
     * The total number of invocations to perform - use this alternatively to
     * {@link #duration()}. The default value is one. @see #duration()
     */
    int invocations() default 1;

    /**
     * The number of milliseconds to run and repeat the test with the full
     * number of configured threads - use this alternatively to
     * {@link #invocations()}. When using a {@link #rampUp()}, the ramp-up times
     * add to the duration.
     * 
     * @see #duration()
     */
    int duration() default -1;

    /**
     * The number of threads which concurrently invoke the test. The default
     * value is 1.
     */
    int threads() default 1;

    /**
     * The number of milliseconds to wait before each thread is added to the
     * currently active threads. On {@link #duration()}-based tests, the total
     * ramp-up time of rampUp * (threads - 1) is added to the configured
     * duration.
     */
    int rampUp() default 0;

    /**
     * The number of milliseconds to wait before the actual measurement and
     * requirements monitoring is activated. Use this to exclude ramp-up times
     * from measurement or wait some minutes before dynamic optimizations are
     * applied (like code optimization or cache population).
     */
    int warmUp() default 0;

    /**
     * Set this to true, if execution should stop with a failure message as soon
     * as a configured {@link Required#max()} value is violated. Set it to
     * false, if you are interested in performing a full measurement to get
     * percentiles, throughput and more. The default value is false.
     */
    boolean cancelOnViolation() default false;

    /**
     * The class of a {@link WaitTimer} implementation by which a wait time can
     * be incurred between test invocations
     */
    Class<? extends WaitTimer> timer() default None.class;

    /**
     * The parameters to initialize the {@link WaitTimer}. The meaning of the
     * values is individual for the WaitTimer implementation.
     */
    double[] timerParams() default {};

    /**
     * One ore more {@link Clock} classes to use for time measurement. The first
     * one specified is the one relevant for requirements verification.
     */
    Class<? extends Clock>[] clocks() default {};

    // TODO v2.x int timeout() default -1;

}
