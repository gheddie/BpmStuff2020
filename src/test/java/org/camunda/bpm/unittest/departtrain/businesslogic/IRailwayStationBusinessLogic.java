package org.camunda.bpm.unittest.departtrain.businesslogic;

public interface IRailwayStationBusinessLogic {

	boolean waggonsReadyToGo(TrainConfig trainConfig);
	
	boolean waggonsAvaiableForShuntingOrder();
}