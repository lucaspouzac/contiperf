/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
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

/**
 * Counts latencies and calculates performance-related statistics.<br/><br/>
 * Created: Created: 14.12.2006 18:11:58
 * @since 1.0
 * @author Volker Bergmann
 */
public final class LatencyCounter {

    private int minLatency;
    private int maxLatency;
    private long latencyCounts[];

    private long startTime;
    private long endTime;
    private long sampleCount;
    private long totalLatency;

    public LatencyCounter() {
        this(1000);
    }

    public LatencyCounter(int expectedMaxLatency) {
        this.latencyCounts = new long[1 + expectedMaxLatency];
        this.sampleCount = 0;
        this.totalLatency = 0;
        this.minLatency = -1;
        this.maxLatency = -1;
        this.startTime = -1;
        this.endTime = -1;
    }
    
    // interface -------------------------------------------------------------------------------------------------------

    public void start() {
    	this.startTime = System.currentTimeMillis();
    }
    
    public void addSample(int latency) {
        if (latency >= latencyCounts.length)
            resize(latency);
        latencyCounts[latency]++;
        sampleCount++;
        totalLatency += latency;
        if (minLatency == -1 || latency < minLatency)
            minLatency = latency;
        if (latency > maxLatency)
            maxLatency = latency;
    }

    public void stop() {
    	this.endTime = System.currentTimeMillis();
    }
    
	public long getStartTime() {
	    return startTime;
    }

    public long getLatencyCount(int latency) {
        if (latency < latencyCounts.length)
            return latencyCounts[latency];
        else
            return 0;
    }

    public double averageLatency() {
        return (double)totalLatency / sampleCount;
    }

    public long minLatency() {
        return minLatency;
    }

    public long maxLatency() {
        return maxLatency;
    }

    public long sampleCount() {
        return sampleCount;
    }

    public long percentileLatency(int percentile) {
        long targetCount = percentile * sampleCount / 100;
        long count = 0;
        for (int value = minLatency; value <= maxLatency; value++) {
            count += getLatencyCount(value);
            if (count >= targetCount)
                return value;
        }
        return maxLatency;
    }
    
    public double throughput() {
    	if (startTime == -1 || endTime == -1)
    		throw new RuntimeException("Invalid setup: Use start() and stop() to indicate test start and end!");
    	return 1000. * sampleCount / duration();
    }

	public long duration() {
	    return endTime - startTime;
    }
    
    // private helpers -------------------------------------------------------------------------------------------------

    private void resize(int requestedIndex) {
        int sizingFactor = (requestedIndex + latencyCounts.length) / latencyCounts.length;
        int newLength = sizingFactor * latencyCounts.length;
        long[] newLatencyCounts = new long[newLength];
        System.arraycopy(latencyCounts, 0, newLatencyCounts, 0, latencyCounts.length);
        latencyCounts = newLatencyCounts;
    }

}
