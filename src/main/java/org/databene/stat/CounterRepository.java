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

package org.databene.stat;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository that binds {@link LatencyCounter}s to a name and makes them available to clients.<br/><br/>
 * Created: 14.01.2011 11:26:09
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class CounterRepository {
	
	private Map<String, LatencyCounter> counters;



	// construction and singleton management ---------------------------------------------------------------------------
	
	private static final CounterRepository INSTANCE = new CounterRepository();
	
	private CounterRepository() {
		counters = new HashMap<String, LatencyCounter>();
	}

	public static CounterRepository getInstance() {
		return INSTANCE;
	}



	// CounterRepository interface -------------------------------------------------------------------------------------
	
	public void addSample(String name, int latency) {
		LatencyCounter counter = getOrCreateCounter(name);
		counter.addSample(latency);
	}

	public LatencyCounter getCounter(String name) {
		return counters.get(name);
	}
	
	public void clear() {
		counters.clear();
	}



	// helper methods --------------------------------------------------------------------------------------------------
	
	private LatencyCounter getOrCreateCounter(String name) {
		LatencyCounter counter = getCounter(name);
		if (counter == null) {
			counter = new LatencyCounter();
			counters.put(name, counter);
		}
		return counter;
	}

}
