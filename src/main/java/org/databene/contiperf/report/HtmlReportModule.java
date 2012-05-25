/*
 * (c) Copyright 2011-2012 by Volker Bergmann. All rights reserved.
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
	private static DecimalFormat lf = new DecimalFormat();



	// ReportModule interface implementation ---------------------------------------------------------------------------

	public void setContext(ReportContext context) {
		this.context = context;
	}

	@Override
	public void completed(String id, LatencyCounter[] counters, PerformanceRequirement requirement) {
		updateReport(id, counters, requirement, context);
	}

	// helper methods --------------------------------------------------------------------------------------------------
	
	private static synchronized void updateReport(String id, LatencyCounter[] counters, PerformanceRequirement requirement, ReportContext context) {
		File reportFile = reportFile();
		if (!initialized || !reportFile.exists())
			initReportFile(reportFile, id, counters, requirement, context);
		else
			extendReportFile(reportFile, id, counters, requirement, context);
	}

	private static File reportFile() {
		return new File(Config.instance().getReportFolder(), REPORT_FILENAME);
	}

	private static void initReportFile(File reportFile, String id, LatencyCounter[] counters, PerformanceRequirement requirement, ReportContext context) {
		initialized = true;
		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(reportFile));
			out.println("<html>");
			out.println("<head>");
			out.println("<title>ContiPerf Report</title>");
			out.println("</head>");
			out.println("<body style='font-family:Verdana;'>");
			out.println("<center>");
			// Render header
			out.println("<h1 style='color:#EE6600'>ContiPerf Report</h1>");
			// render ReportModule links
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
			// Render Help link
			out.println("<a href='http://databene.org/contiperf'>Help</a>");
			out.println("<hr/>");
			out.println("<br/>");
			
			// render overview table
			out.println("<table border='1' cellspacing='0' cellpadding='3px' style='border-color:#eee'>");
			out.println("	<tr>");
			out.println("		<th style='background-color:#ffffdd; color:#EE6600'>&nbsp;&nbsp;&nbsp;</th>");
			out.println("		<th style='background-color:#ffffdd; color:#EE6600'>Test</th>");
			out.println("	<tr>");
			appendHeader(id, counters[0], requirement, out);
			// render overview insertion marker
			out.println(CPF_MARKER_1);
			out.println("</table>");
			
			out.println("<br/>");
			out.println("<hr/>");
			
			// render first entry
			appendEntry(id, counters, requirement, out, context);
			
			// render entry insertion marker
			out.println(CPF_MARKER_2);
			
			// render footer
			out.println("<hr/>");
			out.println("<div style='color:#EE6600;'>Report created by Volker Bergmann's <a href='http://databene.org'>Databene</a> <a href='http://databene.org/contiperf'>ContiPerf</a></div>");
			
			// finish file
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void extendReportFile(File reportFile, String id, LatencyCounter[] counters, PerformanceRequirement requirement, ReportContext context) {
		try {
			// create temp file
			File tempFile = File.createTempFile("index", "html", reportFile.getParentFile());
			PrintWriter out = new PrintWriter(tempFile);
			BufferedReader in = new BufferedReader(new FileReader(reportFile));
			String line;
			
			// insert overview line
			while (!(line = in.readLine()).contains(CPF_MARKER_1))
				out.println(line);
			appendHeader(id, counters[0], requirement, out);
			out.println(line);
			
			// insert entry
			while (!(line = in.readLine()).contains(CPF_MARKER_2))
				out.println(line);
			appendEntry(id, counters, requirement, out, context);
			out.println(line);
			
			// finish temp file and replace original
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

	private static void appendHeader(String id, LatencyCounter counter, PerformanceRequirement requirement, PrintWriter out) {
		out.println("	<tr>");
		out.println("		" + successCell(counter, requirement)); 
		out.println("		<td><a href='#" + id + "'>" + id + "</td>");
		out.println("	<tr>");
	}

	private static String successCell(LatencyCounter counter, PerformanceRequirement requirement) {
		boolean success = ReportUtil.success(counter, requirement);
		return "<td style='background-color:" + (success ? "#00BB00" : "RED") + ";'>&nbsp;</td>";
	}

	private static void appendEntry(String serviceId, LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out, ReportContext context) {
		// render header
		out.println("<a name='" + serviceId + "'><h2 style='color:#EE6600'>" + serviceId + "</h2></a>");
		// render stats table...
		out.println("<table>");
		out.println("	<tr>");
		out.println("		<td>");
		// ...with chart on the left...
		renderStats(serviceId, counters[0], out);
		out.println("		</td>");
		out.println("		<td>");
		// ...and number table on the right
		printStats(serviceId, counters, requirement, out);
		out.println("		</td>");
		out.println("	</tr>");
		out.println("</table>");
		out.println("<br/>");
		
		// render ReportModule links
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

	private static void appendRef(String ref, String label, PrintWriter out) {
		out.print("<a href='" + ref + "'>" + label + "</a>");
	}

	private static void renderStats(String id, LatencyCounter counter, PrintWriter out) {
		String chartUrl = new GoogleLatencyRenderer().render(counter, null, WIDTH, HEIGHT);
		out.println("			<img src='" + chartUrl +"' width='" + WIDTH + "', height='" + HEIGHT + "'/>");
	}

	private static void printStats(String id, LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out) {
		out.println("			<table style='font-family:sans-serif;'>");
		Date startDate = new Date(counters[0].getStartTime());
		out.println("	<tr><th>Started at:</th><td colspan='2'>" + DateFormat.getDateTimeInstance().format(startDate) + "</td></tr>");
		printStatLine("Total invocations:", counters[0].sampleCount(), null, null, null, null, out);
		out.println("	<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>");
		out.println("	<tr valign='top'>");
		out.println("		<th>&nbsp;</th>");
		out.println("		<th>Measured<br/>(" + counters[0].getClockName() + ")</th>");
		out.println("		<th>Required</th>");
		for (int i = 1; i < counters.length; i++)
			out.println("		<th>Measured<br/>(" + counters[i].getClockName() + ")</th>");
		out.println("	</tr>");
		printDurationStats(counters, requirement, out);
		printThroughputStats(counters, requirement, out);
		printMinStats(counters, requirement, out);
		printAverageStats(counters, requirement, out);
		printPercentileStats(counters, requirement, out);
		printMaxStats(counters, requirement, out);
		out.println("			</table>");		
	}

	private static void printDurationStats(LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.totalTimeVerdict(counters[0], requirement);
		Long required = (requirement != null && requirement.getTotalTime() > 0 ? (long) requirement.getTotalTime() : null);
		long[] secondaryValues = null;
		if (counters.length > 1) {
			secondaryValues = new long[counters.length - 1];
			for (int i = 1; i < counters.length; i++)
				secondaryValues[i - 1] = (long) counters[i].duration();
		}
		printStatMsLine("Execution time:", counters[0].duration(), required, secondaryValues, verdict, out);
	}

	private static void printThroughputStats(LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.throughputVerdict(counters[0], requirement);
		Long required = (requirement != null && requirement.getThroughput() > 0 ? (long) requirement.getThroughput() : null);
		long[] secondaryValues = null;
		if (counters.length > 1) {
			secondaryValues = new long[counters.length - 1];
			for (int i = 1; i < counters.length; i++)
				secondaryValues[i - 1] = (long) counters[i].throughput();
		}
		printStatLine("Throughput:", (long) counters[0].throughput(), "/ s", required, secondaryValues, verdict, out);
	}

	private static void printMinStats(LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out) {
		long[] secondaryValues = null;
		if (counters.length > 1) {
			secondaryValues = new long[counters.length - 1];
			for (int i = 1; i < counters.length; i++)
				secondaryValues[i - 1] = (long) counters[i].minLatency();
		}
		printStatMsLine("Min. latency:", counters[0].minLatency(), null, secondaryValues, Verdict.IGNORED, out);
	}

	private static void printAverageStats(LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.averageVerdict(counters[0], requirement);
		Long required = (requirement != null && requirement.getAverage() > 0 ? (long) requirement.getAverage() : null);
		long[] secondaryValues = null;
		if (counters.length > 1) {
			secondaryValues = new long[counters.length - 1];
			for (int i = 1; i < counters.length; i++)
				secondaryValues[i - 1] = (long) counters[i].averageLatency();
		}
		printStatMsLine("Average latency:", (long) counters[0].averageLatency(), required, secondaryValues, verdict, out);
	}

	private static void printPercentileStats(LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out) {
		if (requirement == null || requirement.getPercentileRequirements().length == 0) {
			printPercentileStats(counters, 50, null, out);
			printPercentileStats(counters, 90, null, out);
		} else {
			for (PercentileRequirement percentileRequirement : requirement.getPercentileRequirements())
				printPercentileStats(counters, percentileRequirement.getPercentage(), (long) percentileRequirement.getMillis(), out);
		}
	}

	private static void printPercentileStats(LatencyCounter[] counters, int percentage, Long requiredMillis, PrintWriter out) {
		String label = (percentage == 50 ? "Median:" : percentage + "%:");
		Verdict verdict = ReportUtil.percentileVerdict(counters[0], percentage, requiredMillis);
		long[] secondaryValues = null;
		if (counters.length > 1) {
			secondaryValues = new long[counters.length - 1];
			for (int i = 1; i < counters.length; i++)
				secondaryValues[i - 1] = counters[i].percentileLatency(percentage);
		}
		printStatMsLine(label, counters[0].percentileLatency(percentage), requiredMillis, secondaryValues, verdict, out);
	}

	private static void printMaxStats(LatencyCounter[] counters, PerformanceRequirement requirement, PrintWriter out) {
		Verdict verdict = ReportUtil.maxVerdict(counters[0], requirement);
		Long required = (requirement != null && requirement.getMax() > 0 ? (long) requirement.getMax() : null);
		long[] secondaryValues = null;
		if (counters.length > 1) {
			secondaryValues = new long[counters.length - 1];
			for (int i = 1; i < counters.length; i++)
				secondaryValues[i - 1] = counters[i].maxLatency();
		}
		printStatMsLine("Max latency:", counters[0].maxLatency(), required, secondaryValues , verdict, out);
	}

	private static void printStatMsLine(String label, long mainValue, Long requirement, long[] secondaryValues, Verdict verdict, PrintWriter out) {
		printStatLine(label, mainValue, "ms", requirement, secondaryValues, verdict, out);
	}

	private static void printStatLine(String label, long value, String unit, Long requirement, long[] secondaryValues, Verdict verdict, PrintWriter out) {
		out.println("				<tr>");
		out.println("					<th align='right' valign='top'>" + format(label, verdict) + "</th>");
		out.println("					<td align='right'>" + format(value, unit, verdict) + "</td>");
		out.println("					<td align='right'>" + format(requirement, unit, verdict) + "</td>");
		if (secondaryValues != null)
		for (long secondaryValue : secondaryValues)
			out.println("					<td align='right'>" + format(secondaryValue, unit, null) + "</td>");
		out.println("				</tr>");
	}

	private static String format(Long number, String unit, Verdict verdict) {
		if (number == null)
			return "";
		StringBuilder builder = new StringBuilder();
		builder.append(lf.format(number));
		if (unit != null)
			builder.append(' ').append(unit);
		return format(builder, verdict);
	}

	private static String format(CharSequence text, Verdict verdict) {
		StringBuilder builder = new StringBuilder();
		if (verdict != null) {
			switch (verdict) {
				case SUCCESS : builder.append("<b style='color:#00BB00'>"); break;
				case FAILURE : builder.append("<b style='color:RED'>"); break;
			}
		}
		builder.append(text);
		if (verdict != null && verdict != Verdict.IGNORED)
			builder.append("</b>");
		return builder.toString();
	}

	
	// java.lang.Object overrides --------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
