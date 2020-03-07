package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.List;

public interface IRailwayStationBusinessLogic {

	boolean waggonsReadyToGo(TrainConfig trainConfig);
	
	boolean waggonsAvailableForShuntingOrder(List<String> wagggons);
}