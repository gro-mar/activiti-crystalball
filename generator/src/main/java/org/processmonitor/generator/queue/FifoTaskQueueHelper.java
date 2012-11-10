package org.processmonitor.generator.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

/**
 * Fifo queue helper - helps to generates messages (startProcessInstanceByMessage) based on events. 
 * Event is e.g. queue fulfillment has reached defined level.   
 * only the first message event from the list is generated.
 */
public class FifoTaskQueueHelper {
	
	public static final String LIMIT_OVERFLOWN = "QUEUE_LIMIT_OVERFLOWN";
	TaskService taskService;
	RuntimeService runtimeService;
		
	List<Long> levelLimits = new ArrayList<Long>();
		
	public void addTask(String processDefinitionKey, String taskDefinitionKey, String queueDescriptor) {
		// task is not created yet - we have to increase count
		long itemCount = taskService.createTaskQuery().taskDefinitionKey(taskDefinitionKey).count() + 1;
		for ( long limit : levelLimits )
			if ( itemCount >= limit ) {				
				runtimeService.startProcessInstanceByMessage( LIMIT_OVERFLOWN, getProcessVariables( processDefinitionKey, taskDefinitionKey, 
						limit, itemCount, queueDescriptor));
				return;
			}
	}
	
	
	private static Map<String, Object> getProcessVariables(String processDefinitionKey,
			String taskDefinitionKey, long limit, long itemCount, String queueDescriptor) {
		Map<String, Object> processVariables = new Hashtable<String, Object>();
		processVariables.put("processDefinitionKey", processDefinitionKey);
		processVariables.put("taskDefinitionKey", taskDefinitionKey);
		processVariables.put("limit", limit);
		processVariables.put("itemCount", itemCount);
		processVariables.put("queueDescriptor", queueDescriptor);
		return processVariables;
	}


	public TaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public RuntimeService getRuntimeService() {
		return runtimeService;
	}


	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}


	public List<Long> getLevelLimits() {
		return levelLimits;
	}

	public void setLevelLimits(List<Long> levelLimits) {
		this.levelLimits = levelLimits;
		Collections.sort( levelLimits);
		Collections.reverse( levelLimits);
	}
}
