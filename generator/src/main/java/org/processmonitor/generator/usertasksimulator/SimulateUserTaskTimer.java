package org.processmonitor.generator.usertasksimulator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.processmonitor.generator.usertasksimulator.executor.UserTaskExecutor;

/**
 * Simulate user activity to complete user tasks.
 * simulation runs specifies how many times should simulation run to provide 
 * probability of due time expiration   
 *
 */
public class SimulateUserTaskTimer {

	
	private RepositoryService repositoryService;
	private TaskService taskService;
	private UserTaskExecutor userTaskExecutor; 
	
	/**
	 * default constructor
	 */
	public SimulateUserTaskTimer() {		
	}
	
	public SimulateUserTaskTimer( RepositoryService repositoryService, TaskService taskService, UserTaskExecutor userTaskExecutor) {
		this.setRepositoryService(repositoryService);
		this.setTaskService( taskService);
		this.setUserTaskExecutor( userTaskExecutor );
	}

	/**
	 * simulate from now
	 * 
	 * @param processDefinitionKey
	 * @param simulationRunLimit
	 * @return
	 */
	public List<UserTaskSimulationResults> simulate( String processDefinitionKey, int simulationRunLimit) {
		return simulate( processDefinitionKey, simulationRunLimit, (new Date()).getTime());
	}

	/**
	 * 
	 * @param processDefinitionKey
	 * @param simulationRunLimit count of simulation repetition 
	 * @param currentTime
	 */
	public List<UserTaskSimulationResults> simulate( String processDefinitionKey, int simulationRunLimit, long currentTime ) {

		List<UserTaskSimulationResults> simulationResults = new ArrayList<UserTaskSimulationResults>(simulationRunLimit);
		String processDefinitionId = repositoryService.createProcessDefinitionQuery().processDefinitionKey( processDefinitionKey ).singleResult().getId();
	    ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ( ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition( processDefinitionId ));
	    Map<String,TaskDefinition> taskDefinitions = pde.getTaskDefinitions();

	    for ( int simulationRun = 0; simulationRun < simulationRunLimit; simulationRun++ ) {
			simulationResults.add( runProcessSimulation( taskDefinitions, currentTime) );
		}
	    return simulationResults;
	}
	
	private UserTaskSimulationResults runProcessSimulation(Map<String,TaskDefinition> taskDefinitions, long currentTime) {
		UserTaskSimulationResults result = new UserTaskSimulationResults();
		
		for ( TaskDefinition task : taskDefinitions.values() ) {
			result.addDueTimeReached( task.getKey(), runTaskSimulation( task, currentTime ) );
		}
		return result;
	}

	/**
	 * simulate task execution
	 * @param task
	 * @return was dueTime for given task reached?
	 */
	private Boolean runTaskSimulation(TaskDefinition task, long currentTime) {
		List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey( task.getKey() ).orderByTaskPriority().desc().list();
		Calendar calendar = Calendar.getInstance();
		long simulationTime = currentTime;
		
		for ( Task execTask : tasks ) {
			simulationTime = simulateTaskExecution( execTask, simulationTime);
			calendar.setTimeInMillis( simulationTime );
			if ( (execTask.getDueDate() != null) && (execTask.getDueDate().before( calendar.getTime() )))
					return true;
		}
		return false;
	}

	/**
	 * run user task
	 * 
	 * @param execTask
	 * @param simulationTime
	 * @return
	 */
	private long simulateTaskExecution( Task execTask, long simulationTime) {
		return userTaskExecutor.simulateTaskExecution(execTask, simulationTime);
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public UserTaskExecutor getUserTaskExecutor() {
		return userTaskExecutor;
	}

	public void setUserTaskExecutor(UserTaskExecutor userTaskExecutor) {
		this.userTaskExecutor = userTaskExecutor;
	}
}
