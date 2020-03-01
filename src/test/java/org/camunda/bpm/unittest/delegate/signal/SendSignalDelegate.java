package org.camunda.bpm.unittest.delegate.signal;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendSignalDelegate implements JavaDelegate {

	private static final String SIG_SUB_CATCH = "SIG_SUB_CATCH";

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		execution.getProcessEngine().getRuntimeService().createSignalEvent(SIG_SUB_CATCH).send();
	}
}