/*
 * (c) Copyright 2010-2011 by Volker Bergmann. All rights reserved.
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

import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Replaces the standard {@link BlockJUnit4ClassRunner} for supporting ContiPerf features 
 * in performance test suites.<br/><br/>
 * Created: 02.05.2010 07:54:08
 * @since 1.05
 * @author Volker Bergmann
 */
public class BlockContiPerfClassRunner extends BlockJUnit4ClassRunner {
	
	protected ContiPerfRule rule;

	public BlockContiPerfClassRunner(Class<?> testClass, Object suite) throws InitializationError {
	    super(testClass);
	    rule = new ContiPerfRule(JUnitReportContext.createInstance(suite), suite);
    }
	
	@Override
	protected List<MethodRule> rules(Object test) {
	    List<MethodRule> rules = super.rules(test);
	    boolean configured = false;
	    for (MethodRule targetRule : rules)
	    	if (targetRule instanceof ContiPerfRule) {
	    		ContiPerfRule cpRule = (ContiPerfRule) targetRule;
				if (cpRule.getContext().getReportModules().size() == 0)
	    			cpRule.setContext(rule.getContext());
	    		configured = true;
	    	}
	    if (!configured)
	    	rules.add(rule);
		return rules;
	}

}
