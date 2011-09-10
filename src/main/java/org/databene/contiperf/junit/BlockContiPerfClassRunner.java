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

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.MethodRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Replaces the standard {@link BlockJUnit4ClassRunner} for supporting ContiPerf features 
 * in performance test suites.<br/><br/>
 * Created: 02.05.2010 07:54:08
 * @since 1.05
 * @author Volker Bergmann
 */
@SuppressWarnings("deprecation")
public class BlockContiPerfClassRunner extends BlockJUnit4ClassRunner {
	
	protected ContiPerfRule rule;

	public BlockContiPerfClassRunner(Class<?> testClass, Object suite) throws InitializationError {
	    super(testClass);
	    rule = new ContiPerfRule(JUnitReportContext.createInstance(suite), suite);
    }
	
	/** method taken as is from BlockJUnit4ClassRunner 4.7 
	 * in order to preserve its functionality over following versions */
	protected Statement methodBlock(FrameworkMethod method) {
		Object test;
		try {
			test= new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable e) {
			return new Fail(e);
		}

		Statement statement= methodInvoker(method, test);
		statement= possiblyExpectingExceptions(method, test, statement);
		statement= withPotentialTimeout(method, test, statement);
		statement= withRules(method, test, statement);
		statement= withBefores(method, test, statement);
		statement= withAfters(method, test, statement);
		return statement;
	}
	
	/** method taken as is from BlockJUnit4ClassRunner 4.7 
	 * in order to preserve its functionality over following versions */
	private Statement withRules(FrameworkMethod method, Object target,
			Statement statement) {
		Statement result= statement;
		for (MethodRule each : rules(target))
			result= each.apply(result, method, target);
		return result;
	}
	
	/** actual override feature of this class */
	protected List<MethodRule> rules(Object test) {
	    boolean configured = false;
		List<MethodRule> rules = new ArrayList<MethodRule>();
		for (FrameworkField each : ruleFields()) {
			MethodRule targetRule = createRule(test, each);
	    	if (targetRule instanceof ContiPerfRule) {
	    		ContiPerfRule cpRule = (ContiPerfRule) targetRule;
				if (cpRule.getContext().getReportModules().size() == 0)
	    			cpRule.setContext(rule.getContext());
	    		configured = true;
	    	}
			rules.add(targetRule);
		}
	    if (!configured)
	    	rules.add(rule);
		return rules;
	}

	/** method taken as is from BlockJUnit4ClassRunner 4.7 
	 * in order to preserve its functionality over following versions */
	private List<FrameworkField> ruleFields() {
		return getTestClass().getAnnotatedFields(Rule.class);
	}

	/** method taken as is from BlockJUnit4ClassRunner 4.7 
	 * in order to preserve its functionality over following versions */
	private MethodRule createRule(Object test,
			FrameworkField each) {
		try {
			return (MethodRule) each.get(test);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"How did getFields return a field we couldn't access?");
		}
	}

}
