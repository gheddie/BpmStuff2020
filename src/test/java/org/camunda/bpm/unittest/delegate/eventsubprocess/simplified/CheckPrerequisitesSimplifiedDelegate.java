package org.camunda.bpm.unittest.delegate.eventsubprocess.simplified;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.EventSubProcessTest;

public class CheckPrerequisitesSimplifiedDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		boolean errorRaiser = (boolean) execution.getVariable(EventSubProcessTest.VAR_ERROR_RAISER);
		if (errorRaiser) {
			execution.setVariable(EventSubProcessTest.VAR_PREREQ_CHECKED, false);
		} else {
			execution.setVariable(EventSubProcessTest.VAR_PREREQ_CHECKED, true);
		}
	}
}