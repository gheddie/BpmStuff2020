package org.camunda.bpm.unittest.base;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

public class BpmTestCase {

	protected Task assertSingleTaskPresent(String taskName) {
		List<Task> taskList = taskService().createTaskQuery().taskDefinitionKey(taskName).list();
		assertEquals(1, taskList.size());
		return taskList.get(0);
	}

	protected void assertProcessesRunning(String processDefinitionKey, int count) {
		
		List<ProcessInstance> processInstances = runtimeService().createProcessInstanceQuery().list();
		
		List<ProcessInstance> processInstancesList = runtimeService().createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey).list();
		assertEquals(count, processInstancesList.size());
	}

	protected void fireTimer(String timerName) {

	}
}