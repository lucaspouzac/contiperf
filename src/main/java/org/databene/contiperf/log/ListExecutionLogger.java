/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
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

package org.databene.contiperf.log;

import java.util.ArrayList;
import java.util.List;

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.report.InvocationLog;
import org.databene.contiperf.report.InvocationSummary;
import org.databene.contiperf.report.ListReportModule;

/**
 * {@link ExecutionLogger} implementation that stores all reported invocation logs and 
 * summaries in lists.<br/><br/>
 * Created: 29.03.2010 12:37:33
 * @since 1.0
 * @author Volker Bergmann
 * @deprecated replaced with {@link ListReportModule}
 */
@Deprecated
public class ListExecutionLogger implements ExecutionLogger {
	
	private List<InvocationLog> invocations;
	private List<InvocationSummary> summaries;
	
	public ListExecutionLogger() {
		this.invocations = new ArrayList<InvocationLog>();
		this.summaries = new ArrayList<InvocationSummary>();
    }

	public void logInvocation(String id, int latency, long startTime) {
	    invocations.add(new InvocationLog(id, latency, startTime));
    }

	public void logSummary(String id, long elapsedTime, long invocationCount, long startTime) {
	    summaries.add(new InvocationSummary(id, elapsedTime, invocationCount, startTime));
    }

}
