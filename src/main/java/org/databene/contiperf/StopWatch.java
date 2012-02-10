/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

import org.databene.stat.CounterRepository;
import org.databene.stat.LatencyCounter;

/**
 * Stopwatch-style access to ContiPerf's {@link LatencyCounter} features.
 * A StopWach is created with a name and immediately starts measuring time.
 * When calling stop(), the elapsed time is registered at a central latency 
 * counter identified by the stopwatch's name.
 * <pre>
 *     StopWatch watch = new StopWatch("mytest");
 *     Thread.sleep(delay);
 *     watch.stop();
 * </pre>
 * You can use a stop watch only a single time, so you have to create a new 
 * instance for each measurement you are performing.
 * After the desired number of invocations, you can query the associated 
 * {@link LatencyCounter} from the CounterRepository and query its features, 
 * e.g.
 * <pre>
 *     LatencyCounter counter = CounterRepository.getInstance("mytest");
 *     System.out.println("avg:" + counter.averageLatency + ", max:" + counter.maxLatency())
 * </pre>
 * <br/><br/>
 * Created: 14.01.2011 11:17:30
 * @since 2.0.0
 * @author Volker Bergmann
 * @see CounterRepository
 * @see LatencyCounter
 */
public class StopWatch {

	private String name;
	private long startTime;
	
	public StopWatch(String name) {
		this.name = name;
		this.startTime = System.nanoTime();
	}
	
	public long stop() {
		if (startTime == -1)
			throw new RuntimeException("Called stop() on StopWatch '" + name + "' which has already been stopped");
		int latency = (int) ((System.nanoTime() - startTime) / 1000000L);
		startTime = -1;
		CounterRepository.getInstance().addSample(name, latency);
		return latency;
	}
	
}
