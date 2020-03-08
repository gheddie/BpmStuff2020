package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.List;

public interface IRailwayStationBusinessLogic {

	boolean waggonsReadyToGo(TrainConfig trainConfig);
	
	boolean waggonsAvailableForDepartureTrain(String businessKey, List<String> waggons);
}