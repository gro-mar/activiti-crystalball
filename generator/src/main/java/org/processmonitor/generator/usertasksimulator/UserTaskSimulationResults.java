package org.processmonitor.generator.usertasksimulator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Results for simulation run.
 * Currently it contains only results for simulation of user task nodes  
 *
 */
public class UserTaskSimulationResults implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** map of <taskDefinitionKey, duetimeReached(true/false)> */  
	private Map<String,Boolean> dueTimeReached;

	public UserTaskSimulationResults() {
		dueTimeReached = new HashMap<String, Boolean>();
	}
	public void addDueTimeReached( String taskDefinitionKey, Boolean reached) {
		dueTimeReached.put(taskDefinitionKey, reached);
	}
	
	public boolean isOverDueTime(String taskDefinitionKey) {
		return dueTimeReached.get( taskDefinitionKey) != null ? dueTimeReached.get( taskDefinitionKey) : false;
	}
	public Set<String> getTasks() {
		return dueTimeReached.keySet();
	}
}
