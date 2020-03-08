package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.DepartTrainTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;

public class ProcessCriticalErrorsDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		List<String> plannedWaggons = (List<String>) execution.getVariable(DepartTrainTestCase.VAR_PLANNED_WAGGONS);
		for (String plannedWaggon : plannedWaggons) {
			if (RailwayStationBusinessLogic.getInstance().isWaggonCritical(plannedWaggon)) {
				execution.getProcessEngine().getRuntimeService().startProcessInstanceByMessage(DepartTrainTestCase.MSG_WAG_REP);
			}			
		}
	}
}