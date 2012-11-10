package org.processmonitor.generator.usertasksimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *  postprocess simulation Results
 *
 */
public class UserTaskResultPostProcessor {
	
	/**
	 * return list of activities to highlight
	 * 
	 * @param simulationResults
	 * @return
	 */
	public static List<String> getHighLightedTasks(List<UserTaskSimulationResults> simulationResults, float highlightTreshold) {
		float size = simulationResults.size() / 100f;
		List<String> highlightedTasks = new ArrayList<String>();		
		Map<String, Long> ocurrences = initOcurrences(simulationResults);
		
		// evaluate ocurences
		for ( Entry<String,Long> occurence : ocurrences.entrySet() ) {
			float percentage = occurence.getValue() / size;
			if ( percentage > highlightTreshold )
				highlightedTasks.add( occurence.getKey() );
		}
				
		return highlightedTasks;		
	}

	private static Map<String, Long> initOcurrences(
			List<UserTaskSimulationResults> simulationResults) {
		Map<String, Long> ocurrences = new HashMap<String, Long>();
		
		for ( UserTaskSimulationResults result : simulationResults) {
			for ( String task : result.getTasks() ) {				
				if (result.isOverDueTime( task) ) {
					if (ocurrences.containsKey( task ) ) {
						Long value = ocurrences.get(task);
						ocurrences.put( task, ++value);						
					} else {
						// init ocurrence
						ocurrences.put( task, new Long(1));
					}
				}
			}					
		}
		return ocurrences;
	}
	
	public static Map<String, Object> getNodeDescription(List<UserTaskSimulationResults> simulationResults, float writeDescriptionTreshold) {
		float size = simulationResults.size() / 100f;
		Map<String, Object> writeDescTasks = new HashMap<String,Object>();		
		Map<String, Long> ocurrences = initOcurrences(simulationResults);
		
		// evaluate ocurences
		for ( Entry<String,Long> occurence : ocurrences.entrySet() ) {
			float percentage = occurence.getValue() / size;
			if ( percentage > writeDescriptionTreshold )
				writeDescTasks.put( occurence.getKey(), String.format("%.2f", percentage)+"%" );
		}
				
		return writeDescTasks;		
	}
}
