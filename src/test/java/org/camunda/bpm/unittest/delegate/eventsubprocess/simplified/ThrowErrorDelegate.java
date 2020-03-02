package org.camunda.bpm.unittest.delegate.eventsubprocess.simplified;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.EventSubProcessTest;

public class ThrowErrorDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		throw new BpmnError(EventSubProcessTest.ERROR_CHECK);
	}
}