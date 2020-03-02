package org.camunda.bpm.unittest.delegate.eventsubprocess;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.EventSubProcessTest;

public class CheckPrerequisitesDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		
		boolean errorRaiser = (boolean) execution.getVariable(EventSubProcessTest.VAR_ERROR_RAISER);
		if (errorRaiser) {
			throw new BpmnError(EventSubProcessTest.ERROR_ON_CHECK);
		}
	}
}