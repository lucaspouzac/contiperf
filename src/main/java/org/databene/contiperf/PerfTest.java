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

import org.databene.contiperf.timer.None;

/**
 * Defines execution details and performance requirements for a test method.
 * TODO invocations/duration guidance
 * TODO ramp-up and warm-up advice
 * TODO WaitTimer example
 * <br/><br/>
 * Created: 14.10.2009 14:41:18
 * @since 1.0
 * @author Volker Bergmann
 */
@Documented
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface PerfTest {
	
	/** 
	 * The total number of invocations to perform - use this alternatively to {@link #duration()}. 
	 * The default value is one. @see #duration()
	 */
	int invocations() default  1;
	
	/** 
	 * The number of milliseconds to run and repeat the test with the full number of configured threads - 
	 * use this alternatively to {@link #invocations()}. When using a {@link #rampUp()}, the ramp-up times 
	 * add to the duration.
	 * @see #duration() 
	 */
	int duration() default -1;
	
	/** The number of threads which concurrently invoke the test. The default value is 1. */
	int threads() default  1;
	
	/** 
	 * The number of milliseconds to wait before each thread is added to the currently active threads. 
	 * On {@link #duration()}-based tests, the total ramp-up time of rampUp * (threads - 1) is added to the
	 * configured duration.  
	 */
	int rampUp() default  0;
	
	/** The number of milliseconds to wait before the actual measurement and requirements monitoring is activated.
	 *  Use this to exclude ramp-up times from measurement or wait some minutes before dynamic optimizations are 
	 *  applied (like code optimization or cache population). */
	int warmUp() default  0;
	
	/** Set this to true, if execution should stop with a failure message as soon as a configured {@link Required#max()} 
	 * value is violated. Set it to false, if you are interested in performing a full measurement to get percentiles,  
	 * throughput and more. The default value is false. */
	boolean cancelOnViolation() default false;
	
	/** The class of a {@link WaitTimer} implementation by which a wait time can be incurred between test invocations */
	Class<? extends WaitTimer> timer() default None.class;
	
	/** The parameters to initialize the {@link WaitTimer}. 
	 * The meaning of the values is individual for the WaitTimer implementation. */
	double[] timerParams() default { };
	
	// TODO v2.x int timeout()       default -1;
}
