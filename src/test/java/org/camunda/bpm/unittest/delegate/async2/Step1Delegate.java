package org.camunda.bpm.unittest.delegate.async2;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.unittest.base.JavaJobDelegate;

public class Step1Delegate extends JavaJobDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println(" ---> i am step 1...");
	}
}