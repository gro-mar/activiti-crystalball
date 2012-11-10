package org.processmonitor.generator.usertasksimulator.executor.impl;

import org.activiti.engine.task.Task;
import org.processmonitor.generator.usertasksimulator.executor.UserTaskExecutor;

/**
 * execute any user task in constant time 
 *
 */
public class ConstantUserTaskExecutor implements UserTaskExecutor {

	/** constant execution time */ 
	private long constantExecutionTime = 1;

	public ConstantUserTaskExecutor() {
	}

	public ConstantUserTaskExecutor( long time ) {
		this.constantExecutionTime = time;
	}
	
	public long simulateTaskExecution(Task execTask, long simulationTime) {
		return simulationTime + constantExecutionTime;
	}

	public long getConstantExecutionTime() {
		return constantExecutionTime;
	}

	public void setConstantExecutionTime(long constantExecutionTime) {
		this.constantExecutionTime = constantExecutionTime;
	}

}
