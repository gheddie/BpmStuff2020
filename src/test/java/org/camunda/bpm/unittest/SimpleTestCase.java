package org.camunda.bpm.unittest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

public class SimpleTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	@Test
	@Deployment(resources = { "simpleProcess.bpmn" })
	public void testSimpleProcess() {

		// Given we create a new process instance
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("simpleProcess");
		
		// Then it should be active
		assertThat(processInstance).isActive();
		
		// And it should be the only instance
		assertThat(processInstanceQuery().count()).isEqualTo(1);
		
		// And there should exist just a single task within that process instance
		assertThat(task(processInstance)).isNotNull();

		// When we complete that task
		complete(task(processInstance));
		
		// Then the process instance should be ended...
		assertThat(processInstance).isEnded();
	}
}