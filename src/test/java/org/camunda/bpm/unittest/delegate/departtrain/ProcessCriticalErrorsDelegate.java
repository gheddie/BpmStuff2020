package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class ProcessCriticalErrorsDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		List<String> plannedWaggons = (List<String>) execution.getVariable(DepartTrainProcessConstants.VAR_PLANNED_WAGGONS);
		for (String plannedWaggon : plannedWaggons) {
			if (RailwayStationBusinessLogic.getInstance().isWaggonCritical(plannedWaggon)) {
				HashMap<String, Object> variables = new HashMap<String, Object>();
				// pass master process business key
				String masterProcessBusinessKey = execution.getBusinessKey();
				variables.put(DepartTrainProcessConstants.VAR_DEP_PROC_BK, masterProcessBusinessKey);
				execution.getProcessEngine().getRuntimeService().startProcessInstanceByMessage(DepartTrainProcessConstants.MSG_INVOKE_WAG_REP, variables);
			}			
		}
	}
}