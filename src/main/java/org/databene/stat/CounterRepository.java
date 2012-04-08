/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
	
	public Set<Map.Entry<String, LatencyCounter>> getCounters() {
		return counters.entrySet();
	}
	
	public void clear() {
		counters.clear();
	}

	public void printSummary() {
		DecimalFormat df = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.US));
		List<String[]> list = new ArrayList<String[]>(counters.size());
		List<LatencyCounter> sortedCounters = new ArrayList<LatencyCounter>(counters.values());
		Collections.sort(sortedCounters, new Comparator<LatencyCounter>() {
			public int compare(LatencyCounter c1, LatencyCounter c2) {
				return - new Long(c1.totalLatency()).compareTo(c2.totalLatency());
			}
		});
		for (LatencyCounter counter : sortedCounters) {
			list.add(new String[] {
					counter.getName() + ":", 
					counter.totalLatency() + " ms total,", 
					counter.sampleCount() + " inv,", 
					"avg. " + df.format(counter.averageLatency()) + " ms/inv" });
		}
		printSummaryTable(list);
	}



	// helper methods --------------------------------------------------------------------------------------------------
	
	private LatencyCounter getOrCreateCounter(String name) {
		LatencyCounter counter = getCounter(name);
		if (counter == null)
			counter = createCounter(name);
		return counter;
	}

	private synchronized LatencyCounter createCounter(String name) {
		LatencyCounter counter = getCounter(name);
		if (counter == null) {
			counter = new LatencyCounter(name);
			counters.put(name, counter);
		}
		return counter;
	}

	private void printSummaryTable(List<String[]> list) {
		// determine column widths
		int[] widths = new int[4];
		for (int col = 0; col < 4; col++) {
			int width = 0;
			for (int row = 0; row < list.size(); row++)
				width = Math.max(width, list.get(row)[col].length());
			widths[col] = width;
		}
		// print rows
		for (int row = 0; row < list.size(); row++) {
			for (int col = 0; col < 4; col++) {
				String text = list.get(row)[col];
				if (col > 0) {
					pad(widths[col] - text.length());
					System.out.print(text);
				} else {
					System.out.print(text);
					pad(widths[col] - text.length());
				}
				if (col < 3)
					System.out.print(' ');
			}
			System.out.println();
		}
	}

	private void pad(int count) {
		for (int i = 0; i < count; i++)
			System.out.print(' ');
	}

}
