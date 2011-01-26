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
import org.databene.contiperf.PercentileRequirement;
import org.databene.contiperf.PerformanceRequirement;
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
	
	private static final String CPF_MARKER_1 = "<!-- !!__cpf-marker1__!! -->";
	private static final String CPF_MARKER_2 = "<!-- !!__cpf-marker2__!! -->";

	ReportContext context;
	private static boolean initialized = false;
	DecimalFormat lf = new DecimalFormat();



	// ReportModule interface implementation ---------------------------------------------------------------------------

	public void setContext(ReportContext context) {
		this.context = context;
	}

	@Override
	public void completed(String id, LatencyCounter counter, PerformanceRequirement requirement) {
		updateReport(id, counter, requirement);
	}



	// helper methods --------------------------------------------------------------------------------------------------
	
	private synchronized void updateReport(String id, LatencyCounter counter, PerformanceRequirement requirement) {
		File reportFile = new File(Config.instance().getReportFolder(), REPORT_FILENAME);
		if (!initialized || !reportFile.exists())
			initReportFile(reportFile, id, counter, requirement);
		else
			extendReportFile(reportFile, id, counter, requirement);
	}

	private void initReportFile(File reportFile, String id, LatencyCounter counter, PerformanceRequirement requirement) {
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
			out.println("<table border='1' cellspacing='0' cellpadding='3px' style='border-color:#eee'>");
			out.println("	<tr>");
			out.println("		<th style='background-color:#ffffdd; color:#EE6600'>&nbsp;&nbsp;&nbsp;</th>");
			out.println("		<th style='background-color:#ffffdd; color:#EE6600'>Test</th>");
			out.println("	<tr>");
			appendHeader(id, counter, requirement, out);
			out.println(CPF_MARKER_1);
			out.println("</table>");
			out.println("<br/>");
			out.println("<hr/>");
			appendEntry(id, counter, requirement, out);
			out.println(CPF_MARKER_2);
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
	
	private void extendReportFile(File reportFile, String id, LatencyCounter counter, PerformanceRequirement requirement) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(reportFile));
			File tempFile = File.createTempFile("index", "html", reportFile.getParentFile());
			PrintWriter out = new PrintWriter(tempFile);
			String line;
			while (!(line = in.readLine()).contains(CPF_MARKER_1))
				out.println(line);
			appendHeader(id, counter, requirement, out);
			while (!(line = in.readLine()).contains(CPF_MARKER_2))
				out.println(line);
			appendEntry(id, counter, requirement, out);
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

	private void appendHeader(String id, LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		out.println("	<tr>");
		out.println("		" + successCell(counter, requirement)); 
		out.println("		<td><a href='#" + id + "'>" + id + "</td>");
		out.println("	<tr>");
	}

	private String successCell(LatencyCounter counter, PerformanceRequirement requirement) {
		boolean success = ReportUtil.success(counter, requirement);
		return "<td style='background-color:" + (success ? "#00BB00" : "RED") + ";'>&nbsp;</td>";
	}

	private void appendEntry(String serviceId, LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		out.println("<a name='" + serviceId + "'><h2 style='color:#EE6600'>" + serviceId + "</h2></a>");
		out.println("<table>");
		out.println("	<tr>");
		out.println("		<td>");
		renderStats(serviceId, counter, out);
		out.println("		</td>");
		out.println("		<td>");
		printStats(serviceId, counter, requirement, out);
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

	private void printStats(String id, LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		out.println("			<table style='font-family:sans-serif;'>");
		Date startDate = new Date(counter.getStartTime());
		out.println("	<tr><th>Started at:</th><td colspan='2'>" + DateFormat.getDateTimeInstance().format(startDate) + "</td></tr>");
		out.println("	<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
		out.println("	<tr><th>&nbsp;</th><th>Measured</th><th>Required</th></tr>");
		printDurationStats(counter, requirement, out);
		printStatLine("Total invocations:", "" + counter.sampleCount(), "", out);
		printThroughputStats(counter, requirement, out);
		printStatMsLine("Min. latency:", counter.minLatency(), null, out);
		printAverageStats(counter, requirement, out);
		printPercentileStats(counter, requirement, out);
		printMaxStats(counter, requirement, out);
		out.println("			</table>");		
	}

	private void printDurationStats(LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.totalTimeVerdict(counter, requirement);
		Long required = (requirement != null && requirement.getTotalTime() > 0 ? (long) requirement.getTotalTime() : null);
		printStatMsLine("Execution time:", counter.duration(), required, verdict, out);
	}

	private void printThroughputStats(LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.throughputVerdict(counter, requirement);
		Long required = (requirement != null && requirement.getThroughput() > 0 ? (long) requirement.getThroughput() : null);
		printStatLine("Throughput:", lf.format(counter.throughput()) + " / s", (required != null ? lf.format(required) + " / s" : null), verdict, out);
	}

	private void printAverageStats(LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.averageVerdict(counter, requirement);
		Long required = (requirement != null && requirement.getAverage() > 0 ? (long) requirement.getAverage() : null);
		printStatMsLine("Average latency:", (long) counter.averageLatency(), required, verdict, out);
	}

	private void printPercentileStats(LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		if (requirement == null || requirement.getPercentileRequirements().length == 0) {
			printStatMsLine("Median:", counter.percentileLatency(50), null, ReportUtil.percentileVerdict(counter, 50, null), out);
			printStatMsLine("90%:", counter.percentileLatency(90), null, ReportUtil.percentileVerdict(counter, 90, null), out);
		} else {
			for (PercentileRequirement percentileRequirement : requirement.getPercentileRequirements()) {
				int percentage = percentileRequirement.getPercentage();
				String label = (percentage == 50 ? "Median:" : percentage + "%:");
				long requiredMillis = percentileRequirement.getMillis();
				Verdict verdict = ReportUtil.percentileVerdict(counter, percentage, requiredMillis);
				printStatMsLine(label, counter.percentileLatency(percentage), requiredMillis, verdict, out);
			}
		}
	}

	private void printMaxStats(LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.maxVerdict(counter, requirement);
		Long required = (requirement != null && requirement.getMax() > 0 ? (long) requirement.getMax() : null);
		printStatMsLine("Max latency:", counter.maxLatency(), required, verdict, out);
	}

	private String format(String text, Verdict verdict) {
		StringBuilder builder = new StringBuilder();
		switch (verdict) {
			case SUCCESS : builder.append("<b style='color:#00BB00'>"); break;
			case FAILURE : builder.append("<b style='color:RED'>"); break;
		}
		builder.append(text);
		if (verdict != Verdict.IGNORED)
			builder.append("</b>");
		return builder.toString();
	}

	private void printStatMsLine(String label, long value, Long requirement, Verdict verdict, PrintWriter out) {
		printStatLine(label, lf.format(value) + " ms", (requirement != null ? lf.format(requirement) + " ms" : null), verdict, out);
	}

	private void printStatMsLine(String label, long value, Long requirement, PrintWriter out) {
		printStatLine(label, lf.format(value) + " ms", (requirement != null ? lf.format(requirement) + " ms" : null), out);
	}

	private void printStatLine(String label, String value, String requirement, Verdict verdict, PrintWriter out) {
		out.println("				<tr>");
		out.println("					<th align='right' valign='top'>" + format(label, verdict) + "</th>");
		out.println("					<td align='right'>" + format(value, verdict) + "</td>");
		out.println("					<td align='right'>" + format((requirement != null ? requirement : ""), verdict) + "</td>");
		out.println("				</tr>");
	}

	private void printStatLine(String label, String value, String requirement, PrintWriter out) {
		out.println("				<tr>");
		out.println("					<th align='right' valign='top'>" + label + "</th>");
		out.println("					<td align='right'>" + value + "</td>");
		out.println("					<td align='right'>" + (requirement != null ? requirement : "") + "</td>");
		out.println("				</tr>");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
