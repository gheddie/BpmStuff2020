package org.camunda.bpm.unittest.base;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

public class BpmTestCase {

	protected Task ensureSingleTaskPresent(String taskName) {
		List<Task> taskList = taskService().createTaskQuery().taskDefinitionKey(taskName).list();
		assertEquals(1, taskList.size());
		return taskList.get(0);
	}
	
	protected List<Task> ensureTaskCountPresent(String taskName, int taskCount) {
		List<Task> taskList = taskService().createTaskQuery().taskDefinitionKey(taskName).list();
		assertEquals(taskCount, taskList.size());
		return taskList;
	}

	protected boolean ensureTaskNotPresent(String taskName) {
		return (taskService().createTaskQuery().taskDefinitionKey(taskName).list().size() == 0);
	}

	protected void ensureProcessesRunning(String processDefinitionKey, int count) {
		List<ProcessInstance> processInstancesList = runtimeService().createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey).list();
		assertEquals(count, processInstancesList.size());
	}

	protected void ensureProcessesRunning(int count) {
		List<ProcessInstance> processInstancesList = runtimeService().createProcessInstanceQuery().list();
		assertEquals(count, processInstancesList.size());
	}

	protected void ensureVariableSet(String variableName) {
		assertEquals(1, runtimeService().createVariableInstanceQuery().variableName(variableName).list().size());
	}

	protected void debugEngineState() {

		System.out.println("---------------[ENGINE STATE]------------------");

		// runtime
		System.out.println("[executions] ---> " + runtimeService().createExecutionQuery().list().size());
		System.out.println("[incidents] ---> " + runtimeService().createIncidentQuery().list().size());
		System.out.println("[process instances] ---> " + runtimeService().createProcessInstanceQuery().list().size());
		System.out.println("[variable instances] ---> " + runtimeService().createVariableInstanceQuery().list().size());

		// task
		System.out.println("[tasks] ---> " + taskService().createTaskQuery().list().size());

		// management
		System.out.println("[jobs] ---> " + managementService().createJobQuery().list().size());

		System.out.println("-----------------------------------------------");
	}
}