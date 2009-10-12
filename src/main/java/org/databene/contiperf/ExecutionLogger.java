package org.databene.contiperf;

public interface ExecutionLogger {
	void logSummary(String id, long elapsedTime, long invocationCount, long startTime);
}
