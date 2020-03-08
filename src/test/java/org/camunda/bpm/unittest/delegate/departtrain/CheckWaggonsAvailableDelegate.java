package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;

public class CheckWaggonsAvailableDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.setVariable("allWaggonsAvailable", RailwayStationBusinessLogic.getInstance().waggonsAvailableForDepartureTrain((List<String>) execution.getVariable("plannedWaggons"),
				execution.getBusinessKey()));
	}
}