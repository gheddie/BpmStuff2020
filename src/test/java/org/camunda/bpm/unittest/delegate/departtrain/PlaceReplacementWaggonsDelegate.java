package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class PlaceReplacementWaggonsDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String track = (String) execution.getVariable(DepartTrainProcessConstants.VAR_REPLACE_WAGGON_TARGET_TRACK);
		String[] deliveredReplacementWaggons = (String[]) execution.getVariable(DepartTrainProcessConstants.VAR_DELIVERED_REPLACMENT_WAGGONS);
		List<String> deliveredReplacementWaggonsList = new ArrayList<String>();
		for (String waggonNumber : deliveredReplacementWaggons) {
			deliveredReplacementWaggonsList.add(waggonNumber);
		}
		RailwayStationBusinessLogic.getInstance().addWaggonsToTrack(track, deliveredReplacementWaggonsList);
	}
}