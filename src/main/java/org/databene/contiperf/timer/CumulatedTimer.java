/*
 * (c) Copyright 2012 by Volker Bergmann. All rights reserved.
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

package org.databene.contiperf.timer;

import java.util.Random;

import org.databene.contiperf.WaitTimer;

/**
 * {@link WaitTimer} implementation which provides wait times in a range between min and max with lower probabilities
 * for border values and higher probabilities for values close to the average.<br/><br/>
 * Created: 06.04.2012 17:20:27
 * @since 2.1.0
 * @author Volker Bergmann
 */
public class CumulatedTimer extends AbstractTimer {
	
	private int min   =  500;
	private int range = 1000;
	private Random random = new Random();
	
	public void init(double[] params) {
		checkParamCount(2, params);
		if (params.length > 0)
			min = (int) params[0];
		if (params.length > 1)
			range = (int) (params[1] - min);
	}

	public int getWaitTime() {
		return min + (random.nextInt(range) + random.nextInt(range) + random.nextInt(range)) / 3;
	}

}
