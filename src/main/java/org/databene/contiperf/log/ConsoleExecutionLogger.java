package org.databene.contiperf.log;

import org.databene.contiperf.ExecutionLogger;

public class ConsoleExecutionLogger implements ExecutionLogger {

    public void logSummary(String id, long elapsedTime, long invocationCount, long startTime) {
	    System.out.println(id + ',' + elapsedTime + ',' + invocationCount + ',' + startTime);
    }

}
