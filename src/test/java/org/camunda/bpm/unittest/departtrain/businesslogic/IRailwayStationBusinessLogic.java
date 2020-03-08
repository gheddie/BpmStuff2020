package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.List;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;

public interface IRailwayStationBusinessLogic {

	boolean waggonsAvailableForDepartureTrain(List<String> waggons, String businessKey);
	
	void cancelDepartureOrder(String businessKey);
	
	void removeWaggons(List<String> waggonNumbers);
	
	int countWaggons();
	
	void print(boolean showWaggonDefects);
	
	void setDefectCode(String waggonNumber, WaggonErrorCode waggonErrorCode);
}