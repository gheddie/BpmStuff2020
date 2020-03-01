package org.camunda.bpm.unittest.delegate.async2;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class Step2Delegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("...i am step 2 and i throw an error...");
		throw new BpmnError("123");
	}
}