package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;

public class CancelDepartOrderDelgate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		RailwayStationBusinessLogic.getInstance().cancelDepartureOrder(execution.getBusinessKey());
	}
}