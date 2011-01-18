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

package org.databene.contiperf.log;

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.report.ConsoleReportModule;

/**
 * {@link ExecutionLogger} implementation which writes the execution log to the console.<br/><br/>
 * Created: 12.10.09 08:13:06
 * @since 1.0
 * @author Volker Bergmann
 * @deprecated replaced with {@link ConsoleReportModule}
 */
@Deprecated
public class ConsoleExecutionLogger implements ExecutionLogger {

    public void logSummary(String id, long elapsedTime, long invocationCount, long startTime) {
	    System.out.println(id + ',' + elapsedTime + ',' + invocationCount + ',' + 1000000);
    }

	public void logInvocation(String id, int latency, long startTime) {
	    System.out.println(id + ',' + latency + ',' + 1000000);
    }

}
