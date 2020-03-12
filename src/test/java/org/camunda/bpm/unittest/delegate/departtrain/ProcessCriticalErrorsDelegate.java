package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;
import org.camunda.bpm.unittest.util.HashBuilder;

public class ProcessCriticalErrorsDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		List<String> plannedWaggons = (List<String>) execution.getVariable(DepartTrainProcessConstants.VAR_PLANNED_WAGGONS);
		List<String> waggonsToAssume = new ArrayList<String>();
		String subProcessBusinessKey = null;
		for (String plannedWaggon : plannedWaggons) {
			if (RailwayStationBusinessLogic.getInstance().isWaggonCritical(plannedWaggon)) {
				subProcessBusinessKey = RailwayStationBusinessLogic.getInstance()
						.generateBusinessKey(execution.getProcessInstance().getProcessDefinitionId());
				// pass master process business key to call back...
				execution.getProcessEngine().getRuntimeService().startProcessInstanceByMessage(
						DepartTrainProcessConstants.MSG_INVOKE_WAG_ASSUMEMENT, subProcessBusinessKey,
						HashBuilder.create()
								.withValuePair(DepartTrainProcessConstants.VAR_DEP_PROC_BK, execution.getBusinessKey())
								.withValuePair(DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_ASSUME, plannedWaggon).build());
				// store waggons to repair in 'VAR_WAGGONS_TO_REPAIR'
				waggonsToAssume.add(plannedWaggon);
			}
		}
		execution.setVariable(DepartTrainProcessConstants.VAR_WAGGONS_TO_ASSUME, waggonsToAssume);
	}
}