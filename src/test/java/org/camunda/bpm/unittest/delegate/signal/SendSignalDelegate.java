package org.camunda.bpm.unittest.delegate.signal;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendSignalDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.getProcessEngine().getRuntimeService().createSignalEvent("SIG_SUB_CATCH").send();
	}
}