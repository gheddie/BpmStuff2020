package org.camunda.bpm.unittest.delegate.psychatry;

import java.util.HashMap;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.PsychatryProcessTestCase;

public class InformCaseManagementDelegate implements JavaDelegate {

	private static final String MSG_ADMISSION = "MSG_ADMISSION";

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(PsychatryProcessTestCase.VAR_INTERNAL_ADMISSION, true);
		execution.getProcessEngine().getRuntimeService().startProcessInstanceByMessage(MSG_ADMISSION, variables);
	}
}