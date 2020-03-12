package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.HashMap;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.WaggonRepairInfo;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class StartRepairDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// List<EventSubscription> events = execution.getProcessEngine().getRuntimeService().createEventSubscriptionQuery().list();
		HashMap<String, WaggonRepairInfo> assumements = (HashMap<String, WaggonRepairInfo>) execution.getVariable(DepartTrainProcessConstants.VAR_ASSUMED_WAGGONS);
		for (String key : assumements.keySet()) {
			execution.getProcessEngine().getRuntimeService().correlateMessage(DepartTrainProcessConstants.MSG_START_REPAIR, assumements.get(key).getBusinessKey());
		}
	}
}