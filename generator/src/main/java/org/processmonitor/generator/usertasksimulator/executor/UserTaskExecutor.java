package org.processmonitor.generator.usertasksimulator.executor;

import org.activiti.engine.task.Task;

/**
 * Simulate user task execution 
 *
 */
public interface UserTaskExecutor {
	
	/**
	 * 
	 * @param execTask task to execute 
	 * @param simulationTime current Time
	 * @return time increased by UserTask execution
	 */
	long simulateTaskExecution( Task execTask, long simulationTime);
}