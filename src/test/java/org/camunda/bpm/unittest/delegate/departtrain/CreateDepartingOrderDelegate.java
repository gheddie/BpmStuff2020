package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.List;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.DepartTrainTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailWayException;

public class CreateDepartingOrderDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		List<String> plannedWaggons = null;
		try {
			plannedWaggons = (List<String>) execution.getVariable(DepartTrainTestCase.VAR_PLANNED_WAGGONS);
			RailwayStationBusinessLogic.getInstance().createDepartureOrder(plannedWaggons, execution.getBusinessKey());
		} catch (RailWayException e) {
			throw new BpmnError("ERR_CREATE_DO");
		}
	}
}