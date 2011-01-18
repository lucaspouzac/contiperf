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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.databene.stat.LatencyCounter;

/**
 * {@link ReportModule} which creates a CSV file that reports how often (2nd column) 
 * which latency (1st column) was measured.<br/><br/>
 * Created: 16.01.2011 19:22:23
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class CSVLatencyReportModule extends AbstractReportModule {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private File file;
	OutputStream out;

	public CSVLatencyReportModule() {
		this.file = null;
    }



	// ReportModule interface implementation ---------------------------------------------------------------------------
	
	@Override
	public String getReportReferenceLabel(String serviceId) {
		return (serviceId == null ? null : "Latency distribution as CSV");
	}
	
	@Override
	public String getReportReference(String serviceId) {
		return (serviceId == null ? null : filename(serviceId));
	}
	
	@Override
	public void starting(String serviceId) {
		file = new File(context.getReportFolder(), filename(serviceId));
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			writeHeader(serviceId, out);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void completed(String serviceId, LatencyCounter counter) {
		writeStats(serviceId, counter);
		try {
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing " + file, e);
		}
    }



	// helper methods --------------------------------------------------------------------------------------------------

	private void writeHeader(String serviceId, OutputStream out) {
        String line = "latency,sampleCount" + LINE_SEPARATOR;
		try {
			out.write(line.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeStats(String serviceId, LatencyCounter counter) {
		try {
			for (long i = counter.minLatency(); i <= counter.maxLatency(); i++) {
				String line = i + "," + counter.getLatencyCount(i) + LINE_SEPARATOR;
		        out.write(line.getBytes());
			}
        } catch (IOException e) {
	        e.printStackTrace();
        }
	}
	
	private String filename(String serviceId) {
		return serviceId + ".stat.csv";
	}
	
}
