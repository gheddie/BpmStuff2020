package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.List;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.DepartTrainTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;

public class CheckWaggonsAvailableDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		List<String> plannedWaggonList = (List<String>) execution.getVariable(DepartTrainTestCase.VAR_PLANNED_WAGGON_LIST);
		if (plannedWaggonList == null || plannedWaggonList.size() == 0) {
			throw new BpmnError(DepartTrainTestCase.BPM_ERROR_NO_WAGGONS_PLANNED);
		}
		boolean waggonsAvailable = RailwayStationBusinessLogic.getInstance().waggonsAvailableForShuntingOrder(execution.getBusinessKey(), plannedWaggonList);
		execution.setVariable(DepartTrainTestCase.VAR_ALL_WAGGONS_AVAIABLE, waggonsAvailable);
	}
}