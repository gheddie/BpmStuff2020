package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

public class TerminateTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	private static final String PROCESS_TERMINATE = "terminateTestProcess";

	private static final String TASK_END_TEST_1 = "TaskEndTest1";
	
	private static final String TASK_END_TEST_2 = "TaskEndTest2";

	@Test
	@Deployment(resources = { "terminate/terminateTestProcess.bpmn" })
	public void testTermination() {
		
		runtimeService().startProcessInstanceByKey(PROCESS_TERMINATE);
		
		List<Task> taskList = taskService().createTaskQuery().taskDefinitionKey(TASK_END_TEST_1).list();
		assertEquals(1, taskList.size());
		assertEquals(1, taskService().createTaskQuery().taskDefinitionKey(TASK_END_TEST_2).list().size());
		
		// complete 'TaskEndTest1' --> process must be gone...
		taskService().complete(taskList.get(0).getId());
		assertEquals(0, runtimeService().createExecutionQuery().list().size());
	}
}