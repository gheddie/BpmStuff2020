package org.camunda.bpm.unittest.delegate.psychatrie;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class InformCaseManagementDelegate implements JavaDelegate {

	private static final String MSG_ADMISSION = "MSG_ADMISSION";

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// execution.getProcessEngine().getRuntimeService().correlateMessage(MSG_ADMISSION);
		
		execution.getProcessEngine().getRuntimeService().startProcessInstanceByKey("processCaseManagement");
	}
}