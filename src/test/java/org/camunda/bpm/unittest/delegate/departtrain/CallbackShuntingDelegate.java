package org.camunda.bpm.unittest.delegate.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CallbackShuntingDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		runtimeService().correlateMessage("MSG_SH_DONE");
	}
}