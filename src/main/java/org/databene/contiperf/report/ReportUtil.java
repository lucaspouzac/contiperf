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

import org.databene.contiperf.PercentileRequirement;
import org.databene.contiperf.PerformanceRequirement;
import org.databene.stat.LatencyCounter;

/**
 * Utility class for report modules.<br/><br/>
 * Created: 25.01.2011 19:52:59
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class ReportUtil {

	public static boolean success(LatencyCounter counter, PerformanceRequirement requirement) {
		boolean success = averageVerdict(counter, requirement) != Verdict.FAILURE;
		success &= (maxVerdict(counter, requirement) != Verdict.FAILURE);
		success &= (throughputVerdict(counter, requirement) != Verdict.FAILURE);
		success &= (totalTimeVerdict(counter, requirement) != Verdict.FAILURE);
		if (requirement != null) {
			PercentileRequirement[] percentileRequirements = requirement.getPercentileRequirements();
			for (PercentileRequirement percentileRequirement : percentileRequirements)
				success &= (percentileVerdict(counter, percentileRequirement) != Verdict.FAILURE);
		}
		return success;
	}

	public static Verdict totalTimeVerdict(LatencyCounter counter, PerformanceRequirement requirement) {
		if (requirement == null || requirement.getTotalTime() < 0)
			return Verdict.IGNORED;
		return (counter.duration() <= requirement.getTotalTime() ? Verdict.SUCCESS : Verdict.FAILURE);
	}

	public static Verdict maxVerdict(LatencyCounter counter, PerformanceRequirement requirement) {
		if (requirement == null || requirement.getMax() < 0)
			return Verdict.IGNORED;
		return (counter.maxLatency() <= requirement.getMax() ? Verdict.SUCCESS : Verdict.FAILURE);
	}

	public static Verdict throughputVerdict(LatencyCounter counter, PerformanceRequirement requirement) {
		if (requirement == null || requirement.getThroughput() < 0)
			return Verdict.IGNORED;
		return (counter.throughput() >= requirement.getThroughput() ? Verdict.SUCCESS : Verdict.FAILURE);
	}

	public static Verdict averageVerdict(LatencyCounter counter, PerformanceRequirement requirement) {
		if (requirement == null || requirement.getAverage() < 0)
			return Verdict.IGNORED;
		return (counter.averageLatency() <= requirement.getAverage() ? Verdict.SUCCESS : Verdict.FAILURE);
	}

	public static Verdict percentileVerdict(LatencyCounter counter, PercentileRequirement requirement) {
		if (requirement == null || requirement.getMillis() < 0)
			return Verdict.IGNORED;
		return percentileVerdict(counter, requirement.getPercentage(), (long) requirement.getMillis());
	}

	public static Verdict percentileVerdict(LatencyCounter counter, int percentage, Long requiredMillis) {
		if (requiredMillis == null || requiredMillis < 0)
			return Verdict.IGNORED;
		return (counter.percentileLatency(percentage) <= requiredMillis ? Verdict.SUCCESS : Verdict.FAILURE);
	}

}
