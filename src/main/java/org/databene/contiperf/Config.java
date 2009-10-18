/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
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

package org.databene.contiperf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.databene.contiperf.util.IOUtil;

/**
 * Parses and provides file based test configuration.<br/><br/>
 * Created: 18.10.2009 06:46:31
 * @since 1.0
 * @author Volker Bergmann
 */
public class Config {

	public static final String SYSPROP_ACTIVE = "contiperf.active";
	public static final String SYSPROP_CONFIG_FILENAME = "contiperf.config";
	
	private static final String DEFAULT_CONFIG_FILENAME = "contiperf.properties";
	
	private Map<String, Integer> testInvocationCounts;
	private Map<String, Integer> testTimeouts;
	private int defaultInvocationCount;
	
	
	public Config() {
	    readConfig();
    }

	public boolean active() {
		String sysprop = System.getProperty(SYSPROP_ACTIVE);
		return (sysprop == null || !"false".equals(sysprop.toLowerCase()));
    }

	// helpers ---------------------------------------------------------------------------------------------------------

	private void readConfig() {
	    String filename = configFileName();
    	Properties props = new Properties();
	    InputStream in = inputStream(filename);
	    if (in != null) {
	    	try {
	    		props.load(in);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		IOUtil.close(in);
	    	}
	    }
	    defaultInvocationCount = 1;
		testInvocationCounts = new HashMap<String, Integer>();
		for (Map.Entry<?, ?> entry : props.entrySet()) {
	        String methodName = entry.getKey().toString();
	        int invocationCount = Integer.parseInt(entry.getValue().toString());
	        if ("default".equals(methodName))
	        	defaultInvocationCount = invocationCount;
	        else
	        	testInvocationCounts.put(methodName, invocationCount);
        }
		testTimeouts = new HashMap<String, Integer>();
		// TODO populate testTimeouts
    }
	
	private static InputStream inputStream(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			try {
	            return new FileInputStream(file);
            } catch (FileNotFoundException e) {
	            // This is not supposed to happen. But if it does, we fall back to the context ClassLoader!
            }
		}
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
    }

	private static String configFileName() {
		String filename = System.getProperty(SYSPROP_CONFIG_FILENAME);
		if (filename == null || filename.trim().length() == 0)
			filename = DEFAULT_CONFIG_FILENAME;
		return filename;
	}

	private static Config instance;
	
	public static Config instance() {
	    if (instance == null)
	    	instance = new Config();
	    return instance;
    }

	public int getInvocationCount(String testId) {
	    Integer count = testInvocationCounts.get(testId);
		return (count != null ? count : -1);
    }

}
