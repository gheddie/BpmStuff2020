package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.constant.ProcessConstants;

public class RemoveWaggonsDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		RailwayStationBusinessLogic.getInstance().removeWaggons((List<String>) execution.getVariable(ProcessConstants.VAR_PLANNED_WAGGONS));
	}
}