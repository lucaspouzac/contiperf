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

/**
 * Specifies performance requirements for a test.<br/><br/>
 * Created: 15.10.2009 14:42:57
 * @since 1.0
 * @author Volker Bergmann
 */
@Documented
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface Required {
	
	/** Requires the average number of test executions per second to be the specified value or higher. */
	int throughput()   default -1;

	/** Requires the average test execution time to be of the specified value or less. */
	int average()      default -1;
	
	/** Requires the execution time of 50% of the test executions of the specified value or less. */
	int median()       default -1;
	
	/** Requires each test execution time of the specified value or less. */
	int max()          default -1;
	
	/** Requires the total elapsed time from the beginning of the first test execution to the end of the last one.
	    This does not mean the accumulated response time, but the duration of the test run. */
	int totalTime()    default -1;
	
	/** Requires the execution time of 90% of the test executions of the specified value or less. */
	int percentile90() default -1;

	/** Requires the execution time of 95% of the test executions of the specified value or less. */
	int percentile95() default -1;
	
	/** Requires the execution time of 99% of the test executions of the specified value or less. */
	int percentile99() default -1;
	
	/** Defines a custom set of percentile requirements as a comma-separated list of percentile:millisecond pairs,
	 *  for example 80:300,96:2000 to require 80% of the invocations to take 300 ms or less and 96% to take 2000 ms 
	 *  or less. */
	String percentiles() default "";
	
}
