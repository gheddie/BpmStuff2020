package org.camunda.bpm.unittest.delegate;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class AutomaticReviewDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		throw new BpmnError("123");
	}
}