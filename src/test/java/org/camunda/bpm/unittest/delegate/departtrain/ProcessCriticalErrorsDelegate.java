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
		List<String> waggonsToRepair = new ArrayList<String>();
		for (String plannedWaggon : plannedWaggons) {
			if (RailwayStationBusinessLogic.getInstance().isWaggonCritical(plannedWaggon)) {
				// pass master process business key to call back...
				execution.getProcessEngine().getRuntimeService()
						.startProcessInstanceByMessage(DepartTrainProcessConstants.MSG_INVOKE_WAG_REP, HashBuilder.create()
								.withValue(DepartTrainProcessConstants.VAR_DEP_PROC_BK, execution.getBusinessKey())
								.withValue(DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_REPAIR, plannedWaggon).build());
				// store waggons to repair in 'VAR_WAGGONS_TO_REPAIR'
				waggonsToRepair.add(plannedWaggon);
			}
		}
		execution.setVariable(DepartTrainProcessConstants.VAR_WAGGONS_TO_REPAIR, waggonsToRepair);
	}
}