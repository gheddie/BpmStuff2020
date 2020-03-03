package org.camunda.bpm.unittest.delegate.psychatry;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class RequestCostTakeOverDelegate implements JavaDelegate {
	
	private static final String MSG_TAKE_OVER_REQUESTED = "MSG_TAKE_OVER_REQUESTED";

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.getProcessEngine().getRuntimeService().startProcessInstanceByMessage(MSG_TAKE_OVER_REQUESTED);
	}
}