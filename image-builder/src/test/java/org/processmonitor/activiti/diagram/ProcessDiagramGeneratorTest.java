package org.processmonitor.activiti.diagram;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;


public class ProcessDiagramGeneratorTest extends PluggableActivitiTestCase {

	private static String FINANCIALREPORT_PROCESS_KEY = "financialReport";

	private List<String> processInstanceIds;

	protected void setUp() throws Exception {
		super.setUp();
		repositoryService
				.createDeployment()
				.addClasspathResource(
						"org/activiti/examples/bpmn/usertask/FinancialReportProcess.bpmn20.xml")
				.addClasspathResource(
						"org/activiti/examples/bpmn/usertask/FinancialReportProcess.png")
				.deploy();

		processInstanceIds = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			processInstanceIds.add(runtimeService.startProcessInstanceByKey(
					FINANCIALREPORT_PROCESS_KEY, "BUSINESS-KEY-" + i).getId());
		}
	}

	protected void tearDown() throws Exception {
		for (org.activiti.engine.repository.Deployment deployment : repositoryService
				.createDeploymentQuery().list()) {
			repositoryService.deleteDeployment(deployment.getId(), true);
		}
		super.tearDown();
	}
	
	@Ignore("Generator provides platform dependent images (fonts) see http://forums.activiti.org/en/viewtopic.php?f=6&t=4647&start=0")
	public void testGenerateProcessDefinition() throws IOException {
	    String id = FINANCIALREPORT_PROCESS_KEY;
	    DiagramLayerGenerator generator = new BasicProcessDiagramGenerator( (RepositoryServiceImpl) repositoryService );
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put(AbstractProcessDiagramLayerGenerator.PROCESS_DEFINITION_ID, id);
	    
	    File expectedFile = new File("src/test/resources/org/processmonitor/activiti/diagram/BasicProcessDiagramGeneratorTest.testSimpleProcessDefinition.png" );
	    File generatedFile = new File("target/BasicProcessDiagramGeneratorTest.testSimpleProcessDefinition.png" );
	    ImageIO.write( ImageIO.read(new ByteArrayInputStream( generator.generateLayer("png", params)))
				, "png"
				, generatedFile);
	    assertTrue(FileUtils.contentEquals(expectedFile, generatedFile));
	}
	
	public void testOneNodeHighlight() throws IOException {
	    String id = FINANCIALREPORT_PROCESS_KEY;
	    DiagramLayerGenerator generator = new HighlightNodeDiagramLayer( (RepositoryServiceImpl) repositoryService );
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put( HighlightNodeDiagramLayer.PROCESS_DEFINITION_ID, id);
	    List<String> highlightedActivities = new ArrayList<String>();
	    highlightedActivities.add("writeReportTask");
	    params.put( HighlightNodeDiagramLayer.HIGHLIGHTED_ACTIVITIES, highlightedActivities);
	    
	    File expectedFile = new File("src/test/resources/org/processmonitor/activiti/diagram/HighlightNodeDiagramLayer.testOneNodeHighlight.png" );   
	    File generatedFile = new File("target/HighlightNodeDiagramLayer.testOneNodeHighlight.png" );   
	    ImageIO.write( ImageIO.read(new ByteArrayInputStream( generator.generateLayer("png", params)))
				, "png"
				, generatedFile);
	    assertTrue( FileUtils.contentEquals(expectedFile, generatedFile));	    
	}
	
	public void testMergeLayers() throws IOException {
	    DiagramLayerGenerator generator = new MergeLayersGenerator();
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put( "img1", getBytes("src/test/resources/org/processmonitor/activiti/diagram/MergeLayerGenerator.testMergeLayers1.png"));
	    params.put( "img2", getBytes("src/test/resources/org/processmonitor/activiti/diagram/MergeLayerGenerator.testMergeLayers2.png"));
	    
	    File expectedFile = new File("src/test/resources/org/processmonitor/activiti/diagram/MergeLayerGenerator.testMergeLayers.png" );  
	    File generatedFile = new File("target/MergeLayerGenerator.testMergeLayers.png" );  
	    ImageIO.write( ImageIO.read(new ByteArrayInputStream( generator.generateLayer("png", params)))
				, "png"
				, generatedFile);
	 	assertTrue( FileUtils.contentEquals(expectedFile, generatedFile));
	}
	
	/**
	 * get byte[] from fileName
	 * @param string
	 * @return
	 */
	private Object getBytes(String fileName) {
	    //create file object
	    File file = new File( fileName );
	      FileInputStream fin = null;
		 try
		    {
		      //create FileInputStream object
			 fin = new FileInputStream(fileName);
		     
		      /*
		       * Create byte array large enough to hold the content of the file.
		       * Use File.length to determine size of the file in bytes.
		       */
		     
		     
		       byte fileContent[] = new byte[(int)file.length()];
		     
		       /*
		        * To read content of the file in byte array, use
		        * int read(byte[] byteArray) method of java FileInputStream class.
		        *
		        */
		       fin.read(fileContent);
		     
		       return fileContent;
		    }
		    catch(FileNotFoundException e)
		    {
		      System.out.println("File not found" + e);
		    }
		    catch(IOException ioe)
		    {
		      System.out.println("Exception while reading the file " + ioe);
		    } finally {
		    	if (fin != null) {
		    		try {
						fin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
		    		
		    }
		 	return null;
		 }
	

	@Ignore("Generator provides platform dependent images (fonts) see http://forums.activiti.org/en/viewtopic.php?f=6&t=4647&start=0")
	public void testOneNodeCount() throws IOException {
	    String id = FINANCIALREPORT_PROCESS_KEY;
	    DiagramLayerGenerator generator = new WriteNodeDescriptionDiagramLayer( (RepositoryServiceImpl) repositoryService );
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put( WriteNodeDescriptionDiagramLayer.PROCESS_DEFINITION_ID, id);
	    params.put( "writeReportTask", 5);
	    
	    File expectedFile = new File("src/test/resources/org/processmonitor/activiti/diagram/WriteCountLayerGeneratorTest.oneNumber.png" );
	    File generatedFile = new File("target/WriteCountLayerGeneratorTest.oneNumber.png" );
	    ImageIO.write( ImageIO.read(new ByteArrayInputStream( generator.generateLayer("png", params)))
				, "png"
				, generatedFile);
	    assertTrue( FileUtils.contentEquals(expectedFile, generatedFile));	    	    
	}
}
