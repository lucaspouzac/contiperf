/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.contiperf.util;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.databene.contiperf.Config;
import org.databene.contiperf.ExecutionConfig;
import org.databene.contiperf.PercentileRequirement;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.PerfTestConfigurationError;
import org.databene.contiperf.PerfTestException;
import org.databene.contiperf.PerfTestExecutionError;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.contiperf.Required;

/**
 * Provides I/O utility methods.<br/><br/>
 * Created: 18.10.09 07:43:54
 * @since 1.0
 * @author Volker Bergmann
 */
public class ContiPerfUtil {

	public static void close(Closeable resource) {
	    if (resource != null) {
	    	try {
	    		resource.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
    }

	public static PerfTestException executionError(Throwable e) {
		Throwable result = e;
		if (result instanceof InvocationTargetException)
			result = result.getCause();
		if (result instanceof PerfTestException)
			return (PerfTestException) result;
		else
			return new PerfTestExecutionError(result);
    }

	public static ExecutionConfig mapPerfTestAnnotation(PerfTest annotation) {
		if (annotation != null)
			return new ExecutionConfig(annotation.invocations(), annotation.threads(), annotation.duration(), 
				annotation.cancelOnViolation() /*, annotation.timeout()*/);
		else
			return null;
    }

	public static ExecutionConfig configurePerfTest(PerfTest annotation, String testId) {
		ExecutionConfig config = mapPerfTestAnnotation(annotation);
		if (annotation == null)
			config = new ExecutionConfig(1);
		int count = Config.instance().getInvocationCount(testId);
		if (count >= 0)
			config.setInvocations(count);
		return config;
    }

	public static PerformanceRequirement mapRequired(Required annotation) {
	    if (annotation == null)
	    	return null;
	    int throughput = annotation.throughput();

		int average = annotation.average();
		int max = annotation.max();
		int totalTime = annotation.totalTime();
		
		List<PercentileRequirement> percTmp = new ArrayList<PercentileRequirement>();
		int median = annotation.median();
		if (median > 0)
			percTmp.add(new PercentileRequirement(50, median));
		int percentile90 = annotation.percentile90();
		if (percentile90 > 0)
			percTmp.add(new PercentileRequirement(90, percentile90));
		int percentile95 = annotation.percentile95();
		if (percentile95 > 0)
			percTmp.add(new PercentileRequirement(95, percentile95));
		int percentile99 = annotation.percentile99();
		if (percentile99 > 0)
			percTmp.add(new PercentileRequirement(99, percentile99));

		PercentileRequirement[] customPercs = parsePercentiles(annotation.percentiles());
		for (PercentileRequirement percentile : customPercs)
			percTmp.add(percentile);
		PercentileRequirement[] percs = new PercentileRequirement[percTmp.size()];
		percTmp.toArray(percs);
		return new PerformanceRequirement(average, max, totalTime, percs, throughput);
    }

	public static PercentileRequirement[] parsePercentiles(String percentilesSpec) {
		if (percentilesSpec == null || percentilesSpec.length() == 0)
			return new PercentileRequirement[0];
		String[] assignments = percentilesSpec.split(",");
		PercentileRequirement[] reqs = new PercentileRequirement[assignments.length];
		for (int i = 0; i < assignments.length; i++)
			reqs[i] = parsePercentile(assignments[i]);
	    return reqs;
    }

	private static PercentileRequirement parsePercentile(String assignment) {
	    String[] parts = assignment.split(":");
	    if (parts.length != 2)
	    	throw new PerfTestConfigurationError("Ilegal percentile syntax: " + assignment);
	    int base  = Integer.parseInt(parts[0].trim());
	    int limit = Integer.parseInt(parts[1].trim());
		return new PercentileRequirement(base, limit);
    }
	
}
