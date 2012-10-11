package org.processmonitor.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/org/processmonitor/generator/SimpleProcessDiagramGeneratorTest-context.xml")
public class SimpleProcessDiagramGeneratorTest {

	private static String FINANCIALREPORT_PROCESS_KEY = "financialReport";
	private static String GENERATOR_PROCESS_KEY = "generateProcessDiagram";

	@Autowired 
	private ProcessEngine processEngine;
		
	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Before
	public void before() {

	}

	@After
  public void after() {
    for (Deployment deployment : repositoryService.createDeploymentQuery().list()) {
      repositoryService.deleteDeployment(deployment.getId(), true);
    }
    processEngine.close();
  }

	@Test
	public void testReturnedProcessInstance() throws Throwable {
		// prepare params
		Map<String, Object> params = new Hashtable<String, Object>();
		
		String id = FINANCIALREPORT_PROCESS_KEY;
		params.put( "processDefinitionId", id);
		List<String> highlightedActivities = new ArrayList<String>();
	    highlightedActivities.add("writeReportTask");
	    params.put( "highLightedActivities", highlightedActivities);
	    Map<String, Object> counts = new HashMap<String, Object>();
	    counts.put( "writeReportTask", 5);
	    counts.put( "verifyReportTask", 25);
	    params.put( "counts", counts);
	    params.put( "reportFileName", "target/reportfile.png");
	    
	    // start process
		runtimeService.startProcessInstanceByKey(
				GENERATOR_PROCESS_KEY, "GENERATOR-KEY-1", params).getId();
	    
	}
}
