package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class StartRepairDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		/*
		List<EventSubscription> events = execution.getProcessEngine().getRuntimeService().createEventSubscriptionQuery().list();
		HashMap<String, RepairProcessInfo> assumements = (HashMap<String, RepairProcessInfo>) execution.getVariable(DepartTrainProcessConstants.VAR_ASSUMED_WAGGONS);
		for (String key : assumements.keySet()) {
			execution.getProcessEngine().getRuntimeService().correlateMessage(DepartTrainProcessConstants.MSG_START_REPAIR, assumements.get(key).getBusinessKey());
		}
		*/
	}
}