/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.contiperf.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.stat.LatencyCounter;
import org.junit.Assert;

/**
 * TODO Document class.<br/><br/>
 * Created: 22.10.2009 06:09:07
 * @since 1.0
 * @author Volker Bergmann
 */
@Deprecated
public class Interceptor implements InvocationHandler {

    private final Object target;
    private LatencyCounter counter;
    private String id;
    private ExecutionLogger logger;
    private PerformanceRequirement requirement;

    Interceptor(Object target, String id, PerformanceRequirement requirement, 
    		LatencyCounter counter, ExecutionLogger logger) {
	    this.target = target;
	    this.counter = counter;
	    this.id = id;
	    this.requirement = requirement;
	    this.logger = logger;
    }

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    	int max = (requirement != null ? requirement.getMax() : -1);
	    long start = System.nanoTime();
	    Object result = method.invoke(target, args);
    	int latency = (int) ((System.nanoTime() - start) / 1000000);
		if (max >= 0 && latency > max)
    		Assert.fail("Method " + id + " exceeded time limit of " + 
    				max + "ms running " + latency + " ms");
    	counter.addSample(latency);
    	logger.logInvocation(id, latency, counter.getStartTime());
    	return result;
    }

}
