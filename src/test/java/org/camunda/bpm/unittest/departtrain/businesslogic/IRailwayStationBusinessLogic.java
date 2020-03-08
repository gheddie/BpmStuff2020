package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.util.List;

import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailWayException;

public interface IRailwayStationBusinessLogic {

	void createDepartureOrder(List<String> waggons, String businessKey) throws RailWayException;
	
	void cancelDepartureOrder(String businessKey);
	
	void removeWaggons(List<String> waggonNumbers);
	
	int countWaggons();
	
	void print(boolean showWaggonDefects);
	
	void setDefectCode(String waggonNumber, WaggonErrorCode waggonErrorCode);
}