package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.List;

public interface IRailwayStationBusinessLogic {

	boolean waggonsAvailableForDepartureTrain(List<String> waggons, String businessKey);
	
	void cancelDepartureOrder(String businessKey);
}