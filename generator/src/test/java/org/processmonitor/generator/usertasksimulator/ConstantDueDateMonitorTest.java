package org.processmonitor.generator.usertasksimulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.processmonitor.generator.usertasksimulator.executor.impl.ConstantUserTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/org/processmonitor/generator/usertasksimulator/ConstantDueDateMonitorTest-context.xml")
public class ConstantDueDateMonitorTest {

	private static String PROCESS_KEY = "BasicDueDateSimulationTest";
	private static String MONITOR_PROCESS_KEY = "dueDateMonitor";
	
	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Before
	public void before() {
		// deploy processes
		repositoryService.createDeployment()
	       .addClasspathResource("org/processmonitor/generator/usertasksimulator/BasicDueDateSimulationTest.bpmn")
	       .addClasspathResource("org/processmonitor/generator/SimpleProcessDiagramGenerator.bpmn")
	       .addClasspathResource("org/processmonitor/generator/SimpleProcessDiagramGenerator.png")
	       .addClasspathResource("org/processmonitor/generator/SimpleProcessDiagramGeneratorTest.bpmn")
	       .addClasspathResource("org/processmonitor/generator/usertasksimulator/DueDateMonitor.bpmn")
	       .deploy();
				
		// start processes
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 11, 7, 18, 1, 00);
		Date dueDateFormal =   calendar.getTime();
		calendar.set(2012, 11, 7, 18, 1, 30);
		Date dueDateValue =   calendar.getTime();
		Map<String, Object> variables = new HashMap<String,Object>();
		variables.put("dueDateFormal", dueDateFormal);
		variables.put("dueDateValue", dueDateValue);
		
		for (int i = 0; i < 10; i++) {			
			runtimeService.startProcessInstanceByKey( PROCESS_KEY, "BUSINESS-KEY-" + i, variables);
		}

		// put first 5 tasks to the next node
		List<Task> taskList = taskService.createTaskQuery().list();
		for (int i = 0; i < 5; i++) {
				Task t = taskList.get(i);
				taskService.complete( t.getId() );
		}
	}

	@After
	  public void after() {
	    for (Deployment deployment : repositoryService.createDeploymentQuery().list()) {
	      repositoryService.deleteDeployment(deployment.getId(), true);
	    }
	  }

	@Test
	public void testDueTimeNotReached() throws FileNotFoundException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 11, 7, 18, 00, 00);
		Date currentDate =   calendar.getTime();
		Map<String, Object> params = new Hashtable<String, Object>();
		
		String id = PROCESS_KEY;
		params.put( "processDefinitionId", id);
		params.put( "simulationStartTime", currentDate.getTime());
	    params.put( "reportFileName", "target/ConstantDueDateMoniorTest.dueTimeNotReached.png");
	    
	    // start process
		runtimeService.startProcessInstanceByKey(
				MONITOR_PROCESS_KEY, "MONITOR-KEY-1", params).getId();
	    //check outputs
		//InputStream expectedStream = new FileInputStream("src/test/resources/org/processmonitor/generator/SimpleProcessDiagramGeneratorTestExpected.png" );   
		//InputStream generatedStream = new FileInputStream("target/ConstantDueDateMoniorTest.dueTimeNotReached.png");   
	    //assertTrue( isEqual(expectedStream, generatedStream));	    	

	    	    
	}
	
	@Test
	public void testDueTime1Reached() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 11, 7, 18, 00, 30);
		Date currentDate =   calendar.getTime();
		Map<String, Object> params = new Hashtable<String, Object>();
		
		// get simulator  
		String id = PROCESS_KEY;
		params.put( "processDefinitionId", id);
		params.put( "simulationStartTime", currentDate.getTime());
	    params.put( "reportFileName", "target/ConstantDueDateMoniorTest.dueTime1Reached.png");
	    
	    // start process
		runtimeService.startProcessInstanceByKey(
				MONITOR_PROCESS_KEY, "MONITOR-KEY-2", params).getId();
	}
	
	@Test
	public void testDueTime2Reached() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2012, 11, 7, 18, 00 , 50);
		Date currentDate =   calendar.getTime();
		Map<String, Object> params = new Hashtable<String, Object>();
		
		// get simulator  
		String id = PROCESS_KEY;
		params.put( "processDefinitionId", id);
		params.put( "simulationStartTime", currentDate.getTime());
	    params.put( "reportFileName", "target/ConstantDueDateMoniorTest.dueTime2Reached.png");
	    
	    // start process
		runtimeService.startProcessInstanceByKey(
				MONITOR_PROCESS_KEY, "MONITOR-KEY-3", params).getId();
	}
}
