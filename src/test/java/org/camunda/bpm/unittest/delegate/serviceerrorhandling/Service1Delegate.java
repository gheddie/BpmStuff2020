package org.camunda.bpm.unittest.delegate.serviceerrorhandling;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.ServiceErrorHandlingTestCase;

public class Service1Delegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		boolean errorRaiser = (boolean) execution.getVariable(ServiceErrorHandlingTestCase.VAR_ERROR_RAISER);
		if (errorRaiser) {
			throw new BpmnError(ServiceErrorHandlingTestCase.ERROR_THE_ONLY);			
		}
	}
}