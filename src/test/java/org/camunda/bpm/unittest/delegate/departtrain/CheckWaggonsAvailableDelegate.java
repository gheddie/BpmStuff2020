package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.DepartTrainTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;

public class CheckWaggonsAvailableDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.setVariable(DepartTrainTestCase.VAR_ALL_WAGGONS_AVAIABLE,
				RailwayStationBusinessLogic.getInstance().waggonsAvaiableForShuntingOrder());
	}
}