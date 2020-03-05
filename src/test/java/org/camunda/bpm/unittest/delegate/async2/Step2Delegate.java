package org.camunda.bpm.unittest.delegate.async2;

import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.unittest.Async2TestCase;
import org.camunda.bpm.unittest.base.JavaJobDelegate;

public class Step2Delegate extends JavaJobDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		ProcessEngine processEngine = execution.getProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variables = runtimeService.getVariables(execution.getId());
		boolean raiseError = (boolean) runtimeService.getVariable(execution.getId(), Async2TestCase.VAR_RAISE_ERROR);
		
		if (raiseError) {
			// prevent the error in next execution...
			runtimeService.setVariable(execution.getId(), Async2TestCase.VAR_RAISE_ERROR, false);
			raiseError(true);			
		} else {
			System.out.println("...i am step 2 and i throw ***NO*** error anymore...");			
		}
	}

	private void raiseError(boolean caught) {
		if (caught) {
			// catch it
			System.out.println(" ---> i am step 2 and i throw a ***CAUGHT*** error...");
			throw new BpmnError(Async2TestCase.ERR_CAUGHT);			
		} else {
			// do not catch it
			System.out.println(" ---> i am step 2 and i throw a ***UNCAUGHT*** error...");
			throw new BpmnError(Async2TestCase.ERR_UNCAUGHT);	
		}
	}
}