/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

/**
 * Defines performance requirements on a test.<br/><br/>
 * Created: 18.10.2009 06:21:57
 * @since 1.0
 * @author Volker Bergmann
 */
public class PerformanceRequirement {

	int average;
	int max;
	int totalTime;
	
	PercentileRequirement[] percentiles;

	int throughput;

	public PerformanceRequirement() {
	    this(-1, -1, -1, null, -1);
    }

	public PerformanceRequirement(int average, int max, int totalTime, PercentileRequirement[] percentiles,
            int throughput) {
	    this.average = average;
	    this.max = max;
	    this.totalTime = totalTime;
	    this.percentiles = percentiles;
	    this.throughput = throughput;
    }

    public int getAverage() {
    	return average;
    }

    public int getMax() {
    	return max;
    }

	public void setMax(int max) {
	    this.max = max;
    }
	
    public int getTotalTime() {
    	return totalTime;
    }

    public PercentileRequirement[] getPercentileRequirements() {
    	return percentiles;
    }
    
	public void setPercentileValues(PercentileRequirement[] percentiles) {
	    this.percentiles = percentiles;
    }

    public int getThroughput() {
    	return throughput;
    }

	public void setPercentiles(String percentilesSpec) {
	    setPercentileValues(parsePercentiles(percentilesSpec));
    }

	private PercentileRequirement[] parsePercentiles(String percentilesSpec) {
		String[] assignments = percentilesSpec.split(",");
		PercentileRequirement[] reqs = new PercentileRequirement[assignments.length];
		for (int i = 0; i < assignments.length; i++)
			reqs[i] = parsePercentile(assignments[i]);
	    return reqs;
    }

	private PercentileRequirement parsePercentile(String assignment) {
	    String[] parts = assignment.split(":");
	    if (parts.length != 2)
	    	throw new RuntimeException("Ilegal percentile syntax: " + assignment);
	    int base  = Integer.parseInt(parts[0]);
	    int limit = Integer.parseInt(parts[1]);
		return new PercentileRequirement(base, limit);
    }

}
