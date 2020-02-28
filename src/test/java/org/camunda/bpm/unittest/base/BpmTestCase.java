package org.camunda.bpm.unittest.base;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.task.Task;

public class BpmTestCase {

	protected void checkSingleTaskPresent(String taskName) {
		List<Task> taskList = taskService().createTaskQuery().list();
		assertEquals(1, taskList.size());
		assertEquals(taskName, taskList.get(0).getTaskDefinitionKey());
	}
}