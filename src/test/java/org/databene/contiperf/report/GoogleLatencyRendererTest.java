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

import java.util.Random;

import org.databene.contiperf.report.GoogleLatencyRenderer;
import org.databene.contiperf.report.LatencyDataSet;
import org.databene.stat.LatencyCounter;
import org.junit.Test;

/**
 * Tests the {@link GoogleLatencyRenderer}.<br/><br/>
 * Created: 14.01.2011 12:57:27
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class GoogleLatencyRendererTest {

	@Test
	public void testDataset() {
		LatencyDataSet dataset = new LatencyDataSet(15);
		dataset.addPoint(4, 0);
		dataset.addPoint(5, 1);
		dataset.addPoint(6, 10);
		dataset.addPoint(7, 134);
		dataset.addPoint(8, 156);
		dataset.addPoint(9, 142);
		dataset.addPoint(10, 126);
		dataset.addPoint(11, 60);
		dataset.addPoint(12, 40);
		dataset.addPoint(13, 30);
		dataset.addPoint(14, 10);
		dataset.addPoint(15, 1);
		dataset.addPoint(16, 0);
		dataset.addLabel("med", 10);
		dataset.addLabel("avg", 11);
		dataset.addLabel("90%", 13);
		String url = new GoogleLatencyRenderer().renderDataset(dataset, getClass().getSimpleName(), 400, 300);
		System.out.println(url);
	}
	
	Random random = new Random();

	@Test
	public void testCounter() {
		LatencyCounter counter = new LatencyCounter();
		for (int i = 0; i < 50000; i ++)
			counter.addSample(rand() + rand() + rand() + rand());
		System.out.println(new GoogleLatencyRenderer().render(counter, getClass().getSimpleName(), 400, 300));
	}

	private int rand() {
		return random.nextInt(20) * random.nextInt(20);
	}
	
}
