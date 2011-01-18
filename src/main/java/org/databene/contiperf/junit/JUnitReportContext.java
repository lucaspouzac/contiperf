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

package org.databene.contiperf.junit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.databene.contiperf.Config;
import org.databene.contiperf.report.LoggerModuleAdapter;
import org.databene.contiperf.report.ReportContext;
import org.databene.contiperf.report.ReportModule;

/**
 * JUnit-specific implementation of the ReportContext interface.<br/><br/>
 * Created: 16.01.2011 15:12:24
 * @since 2.0.0
 * @author Volker Bergmann
 */
public class JUnitReportContext extends ReportContext {
	
	public static final Class<PerformanceRequirementFailedError> FAILURE_CLASS = PerformanceRequirementFailedError.class;

	public JUnitReportContext() {
		super(Config.instance().getReportFolder(), FAILURE_CLASS);
	}
	
	public static JUnitReportContext createInstance(Object suite) {
	    List<ReportModule> modules = parseReportModules(suite);
	    JUnitReportContext context = new JUnitReportContext();
		for (ReportModule module : modules)
	    	context.addReportModule(module);
		return context;
    }

	@SuppressWarnings("deprecation")
	private static List<ReportModule> parseReportModules(Object suite) {
		List<ReportModule> modules = new ArrayList<ReportModule>();
		if (suite != null) {
		    for (Field field : suite.getClass().getFields()) {
	    		try {
	    			if (ReportModule.class.isAssignableFrom(field.getType()))
	    				modules.add((ReportModule) field.get(suite));
	    			else if (org.databene.contiperf.ExecutionLogger.class.isAssignableFrom(field.getType()))
	    				modules.add(new LoggerModuleAdapter((org.databene.contiperf.ExecutionLogger) field.get(suite)));
	            } catch (Exception e) {
	                throw new RuntimeException(e);
	            }
		    }	
		}
		// TODO support annotation based ReportModule configuration
	    return modules;
	}
	
}
