/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
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

package org.databene.profile;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.databene.stat.LatencyCounter;

/**
 * Uses a {@link LatencyCounter} to collect profile information and manages sub profiles.<br/><br/>
 * Created: 19.05.2011 09:08:27
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class Profile {
	
	private String name;
	private Profile parent;
	private Map<String, Profile> subProfiles;
	private LatencyCounter counter;
	private DecimalFormat nf = new DecimalFormat("0");
	private DecimalFormat df = new DecimalFormat("0.0");

	public Profile(String name, Profile parent) {
		this.parent = parent;
		this.name = name;
		this.counter = new LatencyCounter(name);
		this.subProfiles = new HashMap<String, Profile>();
	}
	
	public String getName() {
		return name;
	}
	
	public Profile getParent() {
		return parent;
	}
	
	public Collection<Profile> getSubProfiles() {
		return subProfiles.values();
	}

	public Profile getOrCreateSubProfile(String name) {
		Profile result = subProfiles.get(name);
		if (result == null)
			result = createSubProfile(name);
		return result;
	}

	private Profile createSubProfile(String name) {
		Profile result = new Profile(name, this);
		subProfiles.put(name, result);
		return result;
	}

	public void addSample(int duration) {
		counter.addSample(duration);
	}

	public long getInvocationCount() {
		return counter.sampleCount();
	}
	
	public long getTotalLatency() {
		return counter.totalLatency();
	}

	public double getAverageLatency() {
		return counter.averageLatency();
	}

	@Override
	public String toString() {
		return "[" + nf.format(getInvocationCount()) + " inv., " +
				"avg: " + df.format(getAverageLatency()) + ", " +
				"total: " + nf.format(getTotalLatency()) + "]: " + 
				name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Profile that = (Profile) obj;
		return this.name.equals(that.name);
	}
	
}
