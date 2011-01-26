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

package org.databene.contiperf.report;

import java.util.ArrayList;
import java.util.List;

import org.databene.contiperf.PerformanceRequirement;
import org.databene.stat.LatencyCounter;

/**
 * {@link ReportModule} that stores all invocation information in {@link List}s.<br/><br/>
 * Created: 16.01.2011 14:36:48
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class ListReportModule extends AbstractReportModule {
	
	private List<InvocationLog> invocations;
	private List<InvocationSummary> summaries;
	
	public ListReportModule() {
		this.invocations = new ArrayList<InvocationLog>();
		this.summaries = new ArrayList<InvocationSummary>();
    }

	@Override
	public void invoked(String id, int latency, long startTime) {
	    invocations.add(new InvocationLog(id, latency, startTime));
    }

	@Override
	public void completed(String id, LatencyCounter counter, PerformanceRequirement requirement) {
	    summaries.add(new InvocationSummary(id, counter.duration(), counter.sampleCount(), counter.getStartTime()));
    }

	public List<InvocationLog> getInvocations() {
		return invocations;
	}
	
	public List<InvocationSummary> getSummaries() {
		return summaries;
	}
	
}
