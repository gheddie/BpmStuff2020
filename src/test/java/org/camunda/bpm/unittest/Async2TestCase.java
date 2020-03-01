package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class Async2TestCase extends BpmTestCase {
	
	private static final String PROCESS_ASYNC_2 = "asyncTestProcess2";

	private static final String TASK_DO_IT = "TaskDoIt";

	private static final String VAR_STEP_ONE_COUNTER = "STEP_ONE_COUNTER";	
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	@Test
	@Deployment(resources = { "async2/asyncTestProcess2.bpmn" })
	public void testIt() {
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_STEP_ONE_COUNTER, new Integer(0));
		
		runtimeService().startProcessInstanceByKey(PROCESS_ASYNC_2, variables);
		
		List<Task> tasks = taskService().createTaskQuery().taskDefinitionKey(TASK_DO_IT).list();
		assertEquals(1, tasks.size());
		
		// complete user task
		taskService().complete(tasks.get(0).getId());

		// 'Do it' is 'async after' --> process still there...
		assertEquals(1, runtimeService().createExecutionQuery().list().size());
		
		// ...but 'Do It' is not there, because it is on the other side
		// of the transaction border!!
		tasks = taskService().createTaskQuery().taskDefinitionKey(TASK_DO_IT).list();
		assertEquals(0, tasks.size());
		
		// we have job handler of type 'async continuation'
		// pointing on the flow before 'Step 1' 
		// --> ('transition-notify-listener-take$flowDoItStep1')
		List<Job> jobs = managementService().createJobQuery().list();
		assertEquals(1, jobs.size());
	}
}