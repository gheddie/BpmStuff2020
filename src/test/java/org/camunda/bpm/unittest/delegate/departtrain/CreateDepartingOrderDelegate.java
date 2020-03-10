package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.List;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailWayException;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class CreateDepartingOrderDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) {
		
		// initialize 'VAR_SUMMED_UP_ASSUMED_HOURS' here...
		execution.setVariable(DepartTrainProcessConstants.VAR_SUMMED_UP_ASSUMED_HOURS, 0);
		
		List<String> plannedWaggons = null;
		try {
			plannedWaggons = (List<String>) execution.getVariable(DepartTrainProcessConstants.VAR_PLANNED_WAGGONS);
			RailwayStationBusinessLogic.getInstance().createDepartureOrder(plannedWaggons, execution.getBusinessKey());
		} catch (RailWayException e) {
			throw new BpmnError(DepartTrainProcessConstants.ERR_CREATE_DO);
		}
	}
}