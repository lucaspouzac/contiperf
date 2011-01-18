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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Central class for managing report modules as well as aspects that are specific for 
 * a 3rd party testing framework (e.g. JUnit) in which ContiPerf is to be integrated.<br/><br/>
 * Created: 16.01.2011 07:53:38
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class ReportContext {

	private File reportFolder;
	private Constructor<? extends Error> failureCtor;
	private List<ReportModule> modules;
	
	public ReportContext(File reportFolder, Class<? extends Error> failureClass) {
		this.reportFolder = reportFolder;
		try {
			this.failureCtor = failureClass.getConstructor(String.class);
		} catch (SecurityException e) {
			throw new RuntimeException("Security exception in String constructor call of " + failureClass, e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Failure classes need a constructor with one String argument, " 
					+ failureClass + " does not have one", e);
		}
		this.modules = new ArrayList<ReportModule>();
	}
	
	public File getReportFolder() {
		return reportFolder;
	}
	
	public void addReportModule(ReportModule module) {
		module.setContext(this);
		modules.add(module);
	}

	public void fail(String message) {
		try {
			throw failureCtor.newInstance(message);
		} catch (Exception e) {
			throw new RuntimeException("Error creating failure. ", e);
		}
	}
	
	public List<ReportModule> getReportModules() {
		return modules;
	}

	@SuppressWarnings("unchecked")
	public <T extends ReportModule> T getReportModule(Class<T> moduleClass) {
		for (ReportModule module : modules)
			if (moduleClass.isAssignableFrom(module.getClass()))
				return (T) module;
		throw new RuntimeException("No module of type '" + moduleClass.getName() + " found. Available: " + modules);
	}
	
}
