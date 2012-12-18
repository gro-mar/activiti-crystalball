package org.processmonitor.activiti.diagram;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class AuditTrailProcessDiagramGeneratorTest extends PluggableActivitiTestCase {

		private static String FINANCIALREPORT_PROCESS_KEY = "financialReport";

		private String processInstanceId;

		protected void setUp() throws Exception {
			super.setUp();
			repositoryService
					.createDeployment()
					.addClasspathResource(
							"org/activiti/examples/bpmn/usertask/FinancialReportProcess1.bpmn20.xml")
					.addClasspathResource(
							"org/activiti/examples/bpmn/usertask/FinancialReportProcess1.png")
					.deploy();

			processInstanceId =runtimeService.startProcessInstanceByKey(FINANCIALREPORT_PROCESS_KEY, "BUSINESS-KEY-1").getId();
		}

		protected void tearDown() throws Exception {
			for (org.activiti.engine.repository.Deployment deployment : repositoryService
					.createDeploymentQuery().list()) {
				repositoryService.deleteDeployment(deployment.getId(), true);
			}
			super.tearDown();
		}
	@Test
	public void test() throws IOException {
		AuditTrailProcessDiagramGenerator generator = new AuditTrailProcessDiagramGenerator();
		generator.setHistoryService(processEngine.getHistoryService());
		generator.setRepositoryService((RepositoryServiceImpl) processEngine.getRepositoryService());

		Map<String, Object> params = new HashMap<String,Object>();
		params.put( AuditTrailProcessDiagramGenerator.PROCESS_INSTANCE_ID, processInstanceId);
		
		ImageIO.write( ImageIO.read(new ByteArrayInputStream( generator.generateLayer("png", params)))
				, "png"
				, new File( "target/auditTrail1.png"));
		
		int i = 1;
		while (processEngine.getTaskService().createTaskQuery().count() >0 ) {
			Task task = processEngine.getTaskService().createTaskQuery().singleResult();
			processEngine.getTaskService().complete( task.getId());

			File generatedFile = new File( "target/auditTrail"+ ++i +".png");
			
			ImageIO.write( ImageIO.read(new ByteArrayInputStream( generator.generateLayer("png", params)))
					, "png"
					, generatedFile);
			assertTrue(FileUtils.contentEquals(generatedFile, new File("src/test/resources/org/processmonitor/activiti/diagram/auditTrail"+i+".png")));
		}
	}

}
