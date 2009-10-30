/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.databene.contiperf.ExecutionConfig;
import org.databene.contiperf.PercentileRequirement;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.contiperf.Required;

/**
 * Utility method for Annotation processing.<br/><br/>
 * Created: 18.10.2009 06:42:32
 * @since 1.0
 * @author Volker Bergmann
 */
public class AnnotationUtil {

	public static ExecutionConfig mapPerfTestAnnotation(PerfTest annotation) {
		if (annotation == null)
			return null;
		return new ExecutionConfig(annotation.invocations(), annotation.duration(), 
				annotation.timeout(), annotation.cancelOnViolation());
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

		PercentileRequirement[] percs = new PercentileRequirement[percTmp.size()];
		percTmp.toArray(percs);
		return new PerformanceRequirement(average, max, totalTime, percs, throughput);
    }

}
