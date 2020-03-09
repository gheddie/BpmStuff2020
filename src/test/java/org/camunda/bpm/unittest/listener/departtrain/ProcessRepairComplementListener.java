package org.camunda.bpm.unittest.listener.departtrain;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.unittest.departtrain.constant.ProcessConstants;

public class ProcessRepairComplementListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		Map<String, Object> variablesRepairedWaggonsA = new HashMap<String, Object>();
		variablesRepairedWaggonsA.put(ProcessConstants.VAR_SINGLE_WAGGON_TO_REPAIR, "ABC123");
		String executionId = delegateTask.getExecutionId();
		String masterProcessBusinessKey = (String) delegateTask.getProcessEngine().getRuntimeService().getVariable(executionId , ProcessConstants.VAR_DEP_PROC_BK);
		delegateTask.getProcessEngine().getRuntimeService().correlateMessage("MSG_WG_REPAIRED", masterProcessBusinessKey, variablesRepairedWaggonsA);
	}
}