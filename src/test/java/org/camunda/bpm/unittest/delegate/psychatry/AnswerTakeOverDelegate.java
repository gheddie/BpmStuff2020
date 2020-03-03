package org.camunda.bpm.unittest.delegate.psychatry;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class AnswerTakeOverDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.getProcessEngine().getRuntimeService().correlateMessage("MSG_REQ_PROCESSED");
	}
}