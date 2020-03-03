package org.camunda.bpm.unittest.delegate.psychatry;

import java.util.HashMap;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class AnswerTakeOverDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("costTakenOver", (boolean) execution.getVariable("costTakenOver"));
		execution.getProcessEngine().getRuntimeService().correlateMessage("MSG_REQ_PROCESSED", new HashMap<String, Object>(), variables);
	}
}