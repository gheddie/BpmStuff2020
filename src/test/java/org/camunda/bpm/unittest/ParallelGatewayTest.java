package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class ParallelGatewayTest extends BpmTestCase {

	private static final String PROCESS = "parallelGatewayProcess";

	// tasks
	private static final String TASK_A = "TaskA";
	private static final String TASK_B = "TaskB";
	private static final String TASK_C = "TaskC";
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	@Test
	@Deployment(resources = { "parallelGateway/parallelGatewayProcess.bpmn" })
	public void testParallelGateway() {

		runtimeService().startProcessInstanceByKey(PROCESS);
		
		Task taskA = ensureSingleTaskPresent(TASK_A);
		Task taskB = ensureSingleTaskPresent(TASK_B);
		
		taskService().complete(taskA.getId());
		
		// not yet
		ensureTaskNotPresent(TASK_C);
		
		taskService().complete(taskB.getId());
		
		// process there
		assertEquals(1, runtimeService().createProcessInstanceQuery().list().size());
		
		// but now
		taskService().complete(ensureSingleTaskPresent(TASK_C).getId());
		
		// process gone
		assertEquals(0, runtimeService().createProcessInstanceQuery().list().size());
	}
}