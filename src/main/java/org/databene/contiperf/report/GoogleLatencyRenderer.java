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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.databene.contiperf.report.LatencyDataSet.LabelInfo;
import org.databene.stat.LatencyCounter;

/**
 * Formats the latency distribution of a {@link LatencyCounter} using the Google charts API.<br/><br/>
 * Created: 14.01.2011 11:54:18
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class GoogleLatencyRenderer {
	
	public String render(LatencyCounter counter, String title, int width, int height) {
		LatencyDataSet dataset = new LatencyDataSet((int) (counter.maxLatency() - counter.minLatency() + 3));
		for (int i = (int) counter.minLatency(); i <= counter.maxLatency(); i++)
			dataset.addPoint(i, (int) counter.getLatencyCount(i));
		dataset = dataset.reduce(50);
		dataset.addLabel("avg", (int) counter.averageLatency());
		dataset.addLabel("med", (int) counter.percentileLatency(50));
		dataset.addLabel("90%", (int) counter.percentileLatency(90));
		return renderDataset(dataset, title, width, height);
	}
	
	String renderDataset(LatencyDataSet dataset, String title, int width, int height) {
		dataset.scaleY(80);
		try {
			StringBuilder builder = new StringBuilder("http://chart.apis.google.com/chart?cht=lxy"); // xy line chart
			builder.append("&chs=").append(width).append('x').append(height); // image size
			appendData(dataset, builder); // data definition
			builder.append("&chxt=x"); // render x axis only
			builder.append("&chxr=0,0," + dataset.getMaxX()); // x axis scale (#axis, min, max, tick spacing)
			builder.append("&chds=0," + dataset.getMaxX() + ",0,100"); // data scale (min x, max x, min y, max y)
			builder.append("&chf=c,lg,0,FFFFFF,0,FFFF88,1");
			renderLabels(dataset, builder);
			if (title != null)
				builder.append("&chtt=" + URLEncoder.encode(title, "UTF-8")); // title
			return builder.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error encoding title: " + title, e);
		}
	}

	private void renderLabels(LatencyDataSet dataset, StringBuilder builder) {
		builder.append("&chm=B,FFE69B,0,0,0"); // fill
		for (LabelInfo label : dataset.getLabels())
			builder.append("|A" + label.text + ",666666,0," + label.index + ",15"); // labels
	}

	private void appendData(LatencyDataSet dataset, StringBuilder builder) {
		builder.append("&chd=t:");
		for (int i = 0; i < dataset.pointCount(); i++) {
			if (i > 0)
				builder.append(',');
			builder.append(dataset.getX(i));
		}
		builder.append('|');
		for (int i = 0; i < dataset.pointCount(); i++) {
			if (i > 0)
				builder.append(',');
			builder.append(dataset.getY(i));
		}
	}

}
