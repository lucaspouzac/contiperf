package org.databene.contiperf.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.databene.contiperf.ExecutionLogger;
import org.databene.contiperf.Util;

public class FileExecutionLogger implements ExecutionLogger {
	
	private static final String FILENAME = "target/contiperf/contiperf.log";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static boolean firstCall = true;

	public FileExecutionLogger() {
		if (firstCall) {
			File file = new File(FILENAME);
			ensureDirectoryExists(file.getParentFile());	
			if (file.exists())
				file.delete();
			firstCall = false;
		}
    }

	private void ensureDirectoryExists(File dir) {
	    File parent = dir.getParentFile();
	    if (!dir.exists()) {
	    	ensureDirectoryExists(parent);
	    	dir.mkdir();
	    }
    }

	public void logSummary(String id, long elapsedTime, long invocationCount, long startTime) {
		OutputStream out = null;
        String message = id + "," + elapsedTime + ',' + invocationCount + ',' + startTime + LINE_SEPARATOR;
		try {
	        out = new FileOutputStream(FILENAME, true);
	        out.write(message.getBytes());
        } catch (IOException e) {
	        e.printStackTrace();
        } finally {
	        Util.close(out);
        }
    }

}
