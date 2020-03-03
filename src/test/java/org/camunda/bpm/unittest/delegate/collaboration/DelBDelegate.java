package org.camunda.bpm.unittest.delegate.collaboration;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.CollaborationTestCase;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

public class DelBDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		runtimeService().startProcessInstanceByMessage(CollaborationTestCase.MSG_A);
	}
}