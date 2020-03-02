package org.camunda.bpm.unittest.base;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;
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
	
	protected boolean assertTaskNotPresent(String taskName) {
		return (taskService().createTaskQuery().taskDefinitionKey(taskName).list().size() == 0);
	}

	protected void assertProcessesRunning(String processDefinitionKey, int count) {
		
		List<ProcessInstance> processInstances = runtimeService().createProcessInstanceQuery().list();
		
		List<ProcessInstance> processInstancesList = runtimeService().createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey).list();
		assertEquals(count, processInstancesList.size());
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