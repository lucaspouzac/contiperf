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

import java.util.List;

/**
 * Organizes {@link Profile}s in a tree structure.<br/><br/>
 * Created: 19.05.2011 09:01:32
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class Profiler {
	
	private static final Profiler DEFAULT_INSTANCE = new Profiler("default", 1, "ms");

	private long granularity;
	private String unit;
	private Profile rootProfile;
	
	public Profiler(String name, long granularity, String unit) {
		this.granularity = granularity;
		this.unit = unit;
		this.rootProfile = new Profile(name, null);
	}
	
	public static Profiler defaultInstance() {
		return DEFAULT_INSTANCE;
	}
	
	public Profile getRootProfile() {
		return rootProfile;
	}
	
	public void addSample(List<String> path, long duration) {
		int depth = path.size();
		Profile profile = rootProfile;
		for (int i = 0; i < depth; i++)
			profile = profile.getOrCreateSubProfile(path.get(i));
		profile.addSample((int) (duration / granularity));
	}

	public void printSummary() {
		printRecursively(rootProfile, "");
	}

	private void printRecursively(Profile profile, String indent) {
		System.out.println(indent + profile.toString());
		for (Profile subProfile : profile.getSubProfiles())
			printRecursively(subProfile, indent + "  ");
	}

}
