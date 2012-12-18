package org.processmonitor.activiti.diagram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.diagram.ProcessDiagramCanvas;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 *	generate audit trail diagram layer based on audit trail data in historyService 
 *	TODO: Parallel activities
 */
public class AuditTrailProcessDiagramGenerator extends AbstractProcessDiagramLayerGenerator {
	
	protected static Logger log = Logger.getLogger(AuditTrailProcessDiagramGenerator.class.getName());

	protected HistoryService historyService;
	
	public static final String PROCESS_INSTANCE_ID = "process_instance_id";

	/** transition line hight */
	private static final int TRANSITION_HEIGHT = 20;
	private int maxX = 0;
	private int maxY = 0;
	
	//
	// Copied from ProcessDiagramGenerator
	//
	  protected static final Map<String, ActivityDrawXYInstruction> activityDrawInstructions = new HashMap<String, ActivityDrawXYInstruction>();

	  // The instructions on how to draw a certain construct is
	  // created statically and stored in a map for performance.
	  static {
	    // start event
	    activityDrawInstructions.put("startEvent", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawNoneStartEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // start timer event
	    activityDrawInstructions.put("startTimerEvent", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawTimerStartEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });
	    
	    // signal catch
	    activityDrawInstructions.put("intermediateSignalCatch", new ActivityDrawXYInstruction() {
	      
	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawCatchingSignalEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });
	    
	    // signal throw
	    activityDrawInstructions.put("intermediateSignalThrow", new ActivityDrawXYInstruction() {
	      
	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawThrowingSignalEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });
	    
	    // end event
	    activityDrawInstructions.put("endEvent", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawNoneEndEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // error end event
	    activityDrawInstructions.put("errorEndEvent", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawErrorEndEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });
	    
	    // error start event
	    activityDrawInstructions.put("errorStartEvent", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawErrorStartEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });
	    
	    // task
	    activityDrawInstructions.put("task", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });

	    // user task
	    activityDrawInstructions.put("userTask", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawUserTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });

	    // script task
	    activityDrawInstructions.put("scriptTask", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawScriptTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });

	    // service task
	    activityDrawInstructions.put("serviceTask", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawServiceTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });

	    // receive task
	    activityDrawInstructions.put("receiveTask", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawReceiveTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });

	    // send task
	    activityDrawInstructions.put("sendTask", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawSendTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });

	    // manual task
	    activityDrawInstructions.put("manualTask", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawManualTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });
	    
	    // businessRuleTask task
	    activityDrawInstructions.put("businessRuleTask", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawBusinessRuleTask((String) activityImpl.getProperty("name"), x, y, activityImpl.getWidth(),
	                activityImpl.getHeight());
	      }
	    });

	    // exclusive gateway
	    activityDrawInstructions.put("exclusiveGateway", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawExclusiveGateway(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // inclusive gateway
	    activityDrawInstructions.put("inclusiveGateway", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawInclusiveGateway(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // parallel gateway
	    activityDrawInstructions.put("parallelGateway", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawParallelGateway(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // Boundary timer
	    activityDrawInstructions.put("boundaryTimer", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawCatchingTimerEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // Boundary catch error
	    activityDrawInstructions.put("boundaryError", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawCatchingErroEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });
	    
	    // Boundary signal event
	    activityDrawInstructions.put("boundarySignal", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawCatchingSignalEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // timer catch event
	    activityDrawInstructions.put("intermediateTimer", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawCatchingTimerEvent(x, y, activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	    // subprocess
	    activityDrawInstructions.put("subProcess", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        Boolean isExpanded = (Boolean) activityImpl.getProperty(BpmnParse.PROPERTYNAME_ISEXPANDED);
	        Boolean isTriggeredByEvent = (Boolean) activityImpl.getProperty("triggeredByEvent");
	        if(isTriggeredByEvent == null) {
	          isTriggeredByEvent = Boolean.TRUE;
	        }
	        if (isExpanded != null && isExpanded == false) {
	          processDiagramCreator.drawCollapsedSubProcess((String) activityImpl.getProperty("name"), x, y,
	                  activityImpl.getWidth(), activityImpl.getHeight(), isTriggeredByEvent);
	        } else {
	          processDiagramCreator.drawExpandedSubProcess((String) activityImpl.getProperty("name"), x, y,
	                  activityImpl.getWidth(), activityImpl.getHeight(), isTriggeredByEvent);
	        }
	      }
	    });

	    // call activity
	    activityDrawInstructions.put("callActivity", new ActivityDrawXYInstruction() {

	      public void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y) {
	        processDiagramCreator.drawCollapsedCallActivity((String) activityImpl.getProperty("name"), x, y,
	                activityImpl.getWidth(), activityImpl.getHeight());
	      }
	    });

	  }

	  protected static void drawActivity(ProcessDiagramCanvas processDiagramCanvas, ActivityImpl activity, Integer x, Integer y) {
	    String type = (String) activity.getProperty("type");
	    ActivityDrawXYInstruction drawInstruction = activityDrawInstructions.get(type);
	    if (drawInstruction != null) {

	      drawInstruction.draw(processDiagramCanvas, activity, x, y);

	      // Gather info on the multi instance marker
	      boolean multiInstanceSequential = false, multiInstanceParallel = false, collapsed = false;
	      String multiInstance = (String) activity.getProperty("multiInstance");
	      if (multiInstance != null) {
	        if ("sequential".equals(multiInstance)) {
	          multiInstanceSequential = true;
	        } else {
	          multiInstanceParallel = true;
	        }
	      }

	      // Gather info on the collapsed marker
	      Boolean expanded = (Boolean) activity.getProperty(BpmnParse.PROPERTYNAME_ISEXPANDED);
	      if (expanded != null) {
	        collapsed = !expanded;
	      }

	      // Actually draw the markers
	      processDiagramCanvas.drawActivityMarkers(x, y, activity.getWidth(), activity.getHeight(), multiInstanceSequential,
	              multiInstanceParallel, collapsed);

	    }

	    // TODO: Nested activities (boundary events)
//	    
//	    for (ActivityImpl nestedActivity : activity.getActivities()) {
//	      drawActivity(processDiagramCanvas, nestedActivity, highLightedActivities);
//	    }
	  }

	  protected interface ActivityDrawXYInstruction {

		  /**
		   * draw activity on the given position.
		   * 
		   * @see ActivityDrawInstruction
		   * @param processDiagramCreator
		   * @param activityImpl
		   * @param x
		   * @param y
		   */
	    void draw(ProcessDiagramCanvas processDiagramCreator, ActivityImpl activityImpl, int x, int y);

	  }

	
	public byte[] generateLayer(String imageType, Map<String, Object> params) {
		
		// get process instance from which we will get audit trail layer
		final String processInstanceId = (String) params.get( PROCESS_INSTANCE_ID );
	    ProcessDiagramCanvas canvas = generateDiagram( processInstanceId);   
	    
		return convertToByteArray(imageType, canvas.generateImage(imageType));
	}

	protected ProcessDiagramCanvas generateDiagram(	String processInstanceId) {
		if ( historyService.createHistoricProcessInstanceQuery().processInstanceId( processInstanceId).count() == 0) {
			log.info("Process instanceId["+ processInstanceId +"] does not have any history");
			return null;
		}
		
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
	
		final String processDefinitionId  = historicProcessInstance.getProcessDefinitionId();	    
	    ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ( ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition( processDefinitionId ));

		ProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(processInstanceId, processDefinition);

		// TODO: Draw pool shape, if process is participant in collaboration
		// TODO: Draw lanes

		// Draw activities
		List<HistoricActivityInstance> historicActivities = historyService.createHistoricActivityInstanceQuery()
	    		.processInstanceId( processInstanceId)
	    		.orderByHistoricActivityInstanceStartTime()
	    		.asc()
	    		.list();
	    int y = 0;
	    
	    if ( !historicActivities.isEmpty()) {
	    	ActivityImpl activity = findActivity(processDefinition, historicActivities.get(0).getActivityId());
	   
	    	drawActivity( processDiagramCanvas, activity, (maxX - activity.getWidth())/2, y);
		    y += activity.getHeight();
		    
	    	for ( int i=1; i < historicActivities.size(); i++ ) {
	    		// draw transition
	            processDiagramCanvas.drawSequenceflow( maxX/2, y, maxX/2, y+TRANSITION_HEIGHT, false);
			    y += TRANSITION_HEIGHT;
			    
	    		// draw activity
	    		activity = findActivity(processDefinition, historicActivities.get(i).getActivityId());
	    		drawActivity( processDiagramCanvas, activity, (maxX - activity.getWidth())/2, y);
			    y += activity.getHeight();
	    	}
	    } 	

		return processDiagramCanvas;
	}
	
	protected ProcessDiagramCanvas initProcessDiagramCanvas(String processInstanceId,
			ProcessDefinitionEntity processDefinition) {
		int minX = 0;
		int minY = 0;
		maxX = 0;
		maxY = 0;

		//TODO: participant processes are not taken into account

		List<HistoricActivityInstance> historicActivityList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
		for (HistoricActivityInstance historicActivity : historicActivityList) {
			ActivityImpl activity = findActivity( processDefinition, historicActivity.getActivityId());
			if (activity != null) {
				// width
				if (activity.getWidth() > maxX) {
					maxX = activity.getWidth();
				}
				// height
				maxY += activity.getHeight();
	
				// + transition size
				maxY += TRANSITION_HEIGHT;
			}
		}
		
		//TODO:  lanes are not taken into consideration
		maxX += 10;
		// correct image height
		maxY += 10 - TRANSITION_HEIGHT;
		return new ProcessDiagramCanvas(maxX, maxY, minX, minY);
	}
	
	private static ActivityImpl findActivity(ProcessDefinitionEntity processDefinition, String activityId) {
		for (ActivityImpl activity : processDefinition.getActivities()) {
			if ( activity.getId() == activityId)
				return activity;
		}
		return null;
	}
	
	public HistoryService getHistoryService() {
		return historyService;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

}
