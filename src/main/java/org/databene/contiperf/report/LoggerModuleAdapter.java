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

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.stat.LatencyCounter;

/**
 * Adapter class which makes implementors of the old {@link ExecutionLogger} interface 
 * available in ContiPerf 2.<br/><br/> 
 * If you are migrating to ContiPerf, usages of the predefined 
 * ContiPerf {@link ExecutionLogger}s should be replaced with their {@link ReportModule}
 * counterpart. For example, if the old version was
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new ConsoleExecutionLogger());
 * </pre>
 * the new version would be
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new ConsoleReportModule());
 * </pre>
 * <br/>
 * Custom ExecutionLogger implementations still can be used by wrapping them with a {@link LoggerModuleAdapter}.
 * If the old version was
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new MyCustomLogger());
 * </pre>
 * the new version would be
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new LoggerModuleAdapter(new MyCustomLogger()));
 * </pre>
 * <br/><br/>
 * Created: 16.01.2011 08:06:47
 * @since 2.0.0
 * @author Volker Bergmann
 * @see ConsoleReportModule
 * @see CSVSummaryReportModule
 * @see EmptyReportModule
 * @see HtmlReportModule
 */
@SuppressWarnings("deprecation")
public class LoggerModuleAdapter extends AbstractReportModule {
	
	protected ExecutionLogger logger;
	
	public LoggerModuleAdapter(ExecutionLogger logger) {
		this.logger = logger;
	}

	public void invoked(String serviceId, int latency, long startTime) {
		logger.logInvocation(serviceId, latency, startTime);
	}

	@Override
	public void completed(String serviceId, LatencyCounter counter, PerformanceRequirement requirement) {
		logger.logSummary(serviceId, counter.duration(), counter.sampleCount(), counter.getStartTime());
	}

	public ExecutionLogger getLogger() {
		return logger;
	}
	
}
