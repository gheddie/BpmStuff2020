package org.camunda.bpm.unittest;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

public class CollaborationTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	// process
	private static final String PROCESS_COLLABORATION = "processA";
	
	// tasks
	private static final String TASK_A = "TaskA";
	private static final String TASK_C = "TaskC";
	private static final String TASK_E = "TaskE";
	
	// messages
	public static final String MSG_A = "MSG_A";
	public static final String MSG_B = "MSG_B";

	@Test
	@Deployment(resources = { "collaboration/collaboration.bpmn" })
	public void testCollaboration() {

		runtimeService().startProcessInstanceByKey(PROCESS_COLLABORATION);
		
		// 1 process
		ensureProcessesRunning(1);
		
		taskService().complete(ensureSingleTaskPresent(TASK_A).getId());
		
		// 2 processes
		ensureProcessesRunning(2);
		
		taskService().complete(ensureSingleTaskPresent(TASK_C).getId());
		
		// 1 process
		ensureProcessesRunning(1);
		
		taskService().complete(ensureSingleTaskPresent(TASK_E).getId());
		
		// 0 processes
		ensureProcessesRunning(0);
	}
}