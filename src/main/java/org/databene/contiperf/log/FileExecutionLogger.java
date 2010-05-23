/*
 * (c) Copyright 2009-2010 by Volker Bergmann. All rights reserved.
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

package org.databene.contiperf.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.util.ContiPerfUtil;

/**
 * {@link ExecutionLogger} implementation which writes the execution log to a file.<br/><br/>
 * Created: 12.10.09 10:12:39
 * @since 1.0
 * @author Volker Bergmann
 */
public class FileExecutionLogger implements ExecutionLogger {
	
	private static final String FILENAME = "target/contiperf/contiperf.log";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static boolean firstCall = true;
	static AtomicLong invocationCount = new AtomicLong();

	public FileExecutionLogger() {
		if (firstCall) {
			createSummaryFile();
			firstCall = false;
		}
    }

	public void logInvocation(String id, int latency, long startTime) {
		invocationCount.incrementAndGet();
	    System.out.println(id + ',' + latency + ',' + startTime);
    }

	public void logSummary(String id, long elapsedTime, long invocationCount, long startTime) {
		OutputStream out = null;
        String message = id + "," + elapsedTime + ',' 
        	+ invocationCount + ',' + startTime + LINE_SEPARATOR; // TODO v1.x make formatter a strategy
		try {
	        out = new FileOutputStream(FILENAME, true);
	        out.write(message.getBytes());
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
	        ContiPerfUtil.close(out);
        }
    }
	
	public long invocationCount() {
		return invocationCount.get();
	}
	
	// private helpers -------------------------------------------------------------------------------------------------

	private void createSummaryFile() {
	    File file = new File(".", FILENAME);
	    try {
		    ensureDirectoryExists(file.getParentFile());	
		    if (file.exists())
		    	file.delete();
	    } catch (FileNotFoundException e) {
	    	System.out.println("Unable to create directory: " + file.getAbsolutePath());
	    }
    }

	private void ensureDirectoryExists(File dir) throws FileNotFoundException {
	    File parent = dir.getParentFile();
	    if (!dir.exists()) {
	    	if (parent == null)
	    		throw new FileNotFoundException();
	    	ensureDirectoryExists(parent);
	    	dir.mkdir();
	    }
    }

}
