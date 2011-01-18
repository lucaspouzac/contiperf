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

import java.io.File;

import org.databene.stat.LatencyCounter;

/**
 * Abstract parent class for {@link ReportModule}s, which provides 
 * {@link ReportContext} handling and empty implementations of the 
 * other {@link ReportModule} methods.<br/>
 * <br/>
 * Created: 16.01.2011 08:07:21
 * @since 2.0.0
 * @author Volker Bergmann
 */
public abstract class AbstractReportModule implements ReportModule {

	protected ReportContext context;
	
	public void setContext(ReportContext context) {
		this.context = context;
	}

	public String getReportReferenceLabel(String serviceId) {
		return null;
	}
	
	public String getReportReference(String serviceId) {
		return null;
	}

	public void starting(String serviceId) {
		// ignored
	}
	
	public void invoked(String serviceId, int latency, long startTime) {
		// ignored
	}
	
	public void completed(String serviceId, LatencyCounter counter) {
		// ignored
	}

	protected void ensureDirectoryExists(File dir) {
	    if (!dir.exists()) {
		    File parent = dir.getParentFile();
	    	if (parent != null)
	    		ensureDirectoryExists(parent);
	    	dir.mkdir();
	    }
    }

}
