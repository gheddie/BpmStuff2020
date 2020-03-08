package org.camunda.bpm.unittest.delegate.departtrainnew;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;

public class CheckWaggonsAvailableDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.setVariable("allWaggonsAvailable", RailwayStationBusinessLogic.getInstance().waggonsAvailableForDepartureTrain(execution.getBusinessKey(),
				(List<String>) execution.getVariable("plannedWaggons")));
	}
}