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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import org.databene.contiperf.Config;
import org.databene.stat.LatencyCounter;

/**
 * {@link ReportModule} implementation that creates an HTML report of the 
 * performance tests, their requirements, measurements and latency distribution chart.<br/><br/>
 * Created: 14.01.2011 16:29:50
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class HtmlReportModule extends AbstractReportModule {
	
	private static final String REPORT_FILENAME = "index.html";

	private static final int WIDTH = 320;
	private static final int HEIGHT = 240;
	
	private static final String CPF_MARKER = "<!-- !!__cpf-marker__!! -->";

	ReportContext context;
	private static boolean initialized = false;

	
	
	// ReportModule interface implementation ---------------------------------------------------------------------------

	public void setContext(ReportContext context) {
		this.context = context;
	}

	public void completed(String id, LatencyCounter counter) {
		updateReport(id, counter);
	}
	
	
	
	// helper methods --------------------------------------------------------------------------------------------------
	
	private synchronized void updateReport(String id, LatencyCounter counter) {
		File reportFile = new File(Config.instance().getReportFolder(), REPORT_FILENAME);
		if (!initialized || !reportFile.exists())
			initReportFile(reportFile, id, counter);
		else
			extendReportFile(reportFile, id, counter);
	}

	private void initReportFile(File reportFile, String id, LatencyCounter counter) {
		ensureDirectoryExists(reportFile.getParentFile());
		initialized = true;
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(reportFile));
			out.println("<html>");
			out.println("<head>");
			out.println("<title>ContiPerf Report</title>");
			out.println("</head>");
			out.println("<body style='font-family:Verdana;'>");
			out.println("<center>");
			out.println("<h1 style='color:#EE6600'>ContiPerf Report</h1>");
			boolean first = true;
			for (ReportModule module : context.getReportModules()) {
				String ref = module.getReportReference(null);
				if (ref != null) {
					if (!first)
						out.print("&nbsp;|&nbsp;");
					appendRef(ref, module.getReportReferenceLabel(null), out);
					first = false;
				}
			}
			if (!first)
				out.print("&nbsp;|&nbsp;");
			out.println("<a href='http://databene.org/contiperf'>Help</a>");
			out.println("<hr/>");
			out.println("<br/>");
			appendEntry(id, counter, out);
			out.println(CPF_MARKER);
			out.println("<hr/>");
			out.println("<div style='color:#EE6600;'>Report created by Volker Bergmann's <a href='http://databene.org'>Databene</a> <a href='http://databene.org/contiperf'>ContiPerf</a></div>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void extendReportFile(File reportFile, String id, LatencyCounter counter) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(reportFile));
			File tempFile = File.createTempFile("index", "html", reportFile.getParentFile());
			PrintWriter out = new PrintWriter(tempFile);
			String line;
			while (!(line = in.readLine()).contains(CPF_MARKER))
				out.println(line);
			appendEntry(id, counter, out);
			out.println(line);
			while ((line = in.readLine()) != null)
				out.println(line);
			out.close();
			in.close();
			reportFile.delete();
			tempFile.renameTo(reportFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendEntry(String serviceId, LatencyCounter counter, PrintWriter out) {
		out.println("<h2 style='color:#EE6600'>" + serviceId + "</h2>");
		out.println("<table>");
		out.println("	<tr>");
		out.println("		<td>");
		renderStats(serviceId, counter, out);
		out.println("		</td>");
		out.println("		<td>");
		printStats(serviceId, counter, out); // TODO report requirements
		out.println("		</td>");
		out.println("	</tr>");
		out.println("</table>");
		out.println("<br/>");
		boolean first = true;
		for (ReportModule module : context.getReportModules()) {
			String ref = module.getReportReference(serviceId);
			if (ref != null) {
				if (!first)
					out.print("&nbsp;|&nbsp;");
				String label = module.getReportReferenceLabel(serviceId);
				appendRef(ref, label, out);
				first = false;
			}
		}
		out.println("<br/><br/><br/>");
	}

	private void appendRef(String ref, String label, PrintWriter out) {
		out.print("<a href='" + ref + "'>" + label + "</a>");
	}

	private void renderStats(String id, LatencyCounter counter, PrintWriter out) {
		String chartUrl = new GoogleLatencyRenderer().render(counter, null, WIDTH, HEIGHT);
		out.println("			<img src='" + chartUrl +"' width='" + WIDTH + "', height='" + HEIGHT + "'/>");
	}

	private void printStats(String id, LatencyCounter counter, PrintWriter out) {
		DecimalFormat df = new DecimalFormat("0.#");
		out.println("			<table>");
		Date startDate = new Date(counter.getStartTime());
		printStatLine("Started at:", DateFormat.getDateInstance().format(startDate) + "<br/>" + DateFormat.getTimeInstance().format(startDate), out);
		printStatMsLine("Execution time:", counter.duration(), out);
		printStatLine("Total invocations:", "" + counter.sampleCount(), out);
		printStatMsLine("min. latency:",counter.minLatency(), out);
		printStatLine("avg. latency:", df.format(counter.averageLatency()) + " ms", out);
		printStatMsLine("median:", counter.percentileLatency(50), out);
		printStatMsLine("90%:", counter.percentileLatency(90), out);
		printStatMsLine("95%:", counter.percentileLatency(95), out);
		printStatMsLine("max latency:", counter.maxLatency(), out);
		out.println("			</table>");		
	}

	private void printStatMsLine(String label, long value, PrintWriter out) {
		DecimalFormat lf = new DecimalFormat();
		printStatLine(label, lf.format(value) + " ms", out);
	}

	private void printStatLine(String label, String value, PrintWriter out) {
		out.println("				<tr>");
		out.println("					<th align='right' valign='top'>" + label + "</th>");
		out.println("					<td align='right'>" + value + "</td>");
		out.println("				</tr>");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
