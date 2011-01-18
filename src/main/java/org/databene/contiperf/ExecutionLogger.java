/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

import org.databene.contiperf.report.CSVSummaryReportModule;
import org.databene.contiperf.report.ConsoleReportModule;
import org.databene.contiperf.report.EmptyReportModule;
import org.databene.contiperf.report.HtmlReportModule;
import org.databene.contiperf.report.LoggerModuleAdapter;
import org.databene.contiperf.report.ReportModule;

/**
 * Deprecated Observer interface for ContiPerf 1.x.<br/><br/>
 * Created: 12.10.2009 08:11:23
 * @since 1.0
 * @author Volker Bergmann
 * @deprecated Replaced with {@link ReportModule}. 
 * When using a predefined ExecutionLogger, replace it with the corresponding ReportModule.
 * If the old version was
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new ConsoleExecutionLogger());
 * </pre>
 * the new version would be
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new ConsoleReportModule());
 * </pre>
 * Custom ExecutionLogger implementations still can be used by wrapping them with a {@link LoggerModuleAdapter}.
 * If the old version was
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new MyCustomLogger());
 * </pre>
 * the new version would be
 * <pre>
 *     @Rule public ContiPerfRule = new ContiPerfRule(new LoggerModuleAdapter(new MyCustomLogger()));
 * </pre>
 * @see ConsoleReportModule
 * @see EmptyReportModule
 * @see CSVSummaryReportModule
 * @see HtmlReportModule
 */
@Deprecated
public interface ExecutionLogger {
	void logInvocation(String id, int latency, long startTime);
	void logSummary(String id, long elapsedTime, long invocationCount, long startTime);
}
