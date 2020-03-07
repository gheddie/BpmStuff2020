package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.List;

public interface IRailwayStationBusinessLogic {

	boolean waggonsReadyToGo(TrainConfig trainConfig);
	
	boolean waggonsAvailableForShuntingOrder(String businessKey, List<String> waggons);
}