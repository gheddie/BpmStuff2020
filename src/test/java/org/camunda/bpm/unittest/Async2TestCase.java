package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class Async2TestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	private static final String PROCESS_ASYNC_2 = "asyncTestProcess2";

	// tasks
	private static final String TASK_DO_IT = "TaskDoIt";

	// errors
	public static final String ERR_CAUGHT = "ERR_CAUGHT";
	public static final String ERR_UNCAUGHT = "ERR_UNCAUGHT";
	
	// variabls
	public static final String VAR_RAISE_ERROR = "raiseError";
	
	@Test
	@Deployment(resources = { "async2/asyncTestProcess2.bpmn" })
	public void testIt() {
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_RAISE_ERROR, true);
		runtimeService().startProcessInstanceByKey(PROCESS_ASYNC_2, variables);
		
		taskService().complete(ensureSingleTaskPresent(TASK_DO_IT).getId());
		
		// 'Do it' is 'async after' --> process still there...
		assertEquals(1, runtimeService().createExecutionQuery().list().size());
		
		// ...but 'Do It' is not there, because it is on the other side
		// of the transaction border!!
		ensureTaskNotPresent(TASK_DO_IT);
		
		// we have job handler of type 'async continuation'
		// pointing on the flow before 'Step 1' 
		// --> ('transition-notify-listener-take$flowDoItStep1')
		List<Job> jobs = managementService().createJobQuery().list();
		assertEquals(1, jobs.size());
		
		Job theJob = managementService().createJobQuery().list().get(0);
		System.out.println(" ---> job retries is set to: " + theJob.getRetries());
		
		// execute job (try 1)?
		managementService().executeJob(theJob.getId());
		
		assertEquals(0, managementService().createJobQuery().list().size());
	}
}