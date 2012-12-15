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

package org.databene.contiperf.sensor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

/**
 * TODO Document class.<br/><br/>
 * Created: 13.12.2012 13:31:56
 * @since TODO version
 * @author Volker Bergmann
 */
public class MemorySensor {
	
	private static final int DEFAULT_INTERVAL = 60000;
	
	private static final MemorySensor INSTANCE = new MemorySensor();
	
	public static MemorySensor getInstance() {
		return INSTANCE;
	}
	
	private MeasurementThread thread;
	private long maxUsedHeapSize;
	private long maxCommittedHeapSize;
	
	public MemorySensor() {
		startThread(DEFAULT_INTERVAL);
		reset();
	}

	public int getInterval() {
		return thread.getInterval();
	}
	
	public void setInterval(int interval) {
		if (interval != getInterval()) {
			thread.cancel();
			startThread(interval);
		}
	}
	
	public long getMaxUsedHeapSize() {
		return maxUsedHeapSize;
	}
	
	public long getMaxCommittedHeapSize() {
		return maxCommittedHeapSize;
	}
	
	public void reset() {
		maxUsedHeapSize = 0;
		maxCommittedHeapSize = 0;
		measure();
	}
	
	public void measure() {
		MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		this.maxUsedHeapSize = Math.max(maxUsedHeapSize, heapMemoryUsage.getUsed());
		this.maxCommittedHeapSize = Math.max(maxCommittedHeapSize, heapMemoryUsage.getCommitted());
	}
	
	private void startThread(int interval) {
		this.thread = new MeasurementThread(interval);
		thread.start();
	}
	
	class MeasurementThread extends Thread {
		
		private int interval;
		
		public MeasurementThread(int interval) {
			this.interval = interval;
		}

		public int getInterval() {
			return interval;
		}

		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					measure();
					Thread.sleep(interval);
				}
			} catch (InterruptedException e) {
				// makes the thread leave the loop and finish
			}
		}
		
		public void cancel() {
			interrupt();
		}
		
	}
	
}
