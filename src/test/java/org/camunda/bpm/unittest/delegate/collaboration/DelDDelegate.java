package org.camunda.bpm.unittest.delegate.collaboration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.CollaborationTest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

public class DelDDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		runtimeService().correlateMessage(CollaborationTest.MSG_B);
	}
}