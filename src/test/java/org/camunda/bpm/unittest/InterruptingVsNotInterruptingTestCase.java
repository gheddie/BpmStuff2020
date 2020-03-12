package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
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

public class InterruptingVsNotInterruptingTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	// processes
	private static final String PROCESS = "interruptingVsNotInterruptingProcess";
	
	// variables
	private static final String VAR_INTERRUPTING = "interrupting";
	
	// tasks
	private static final String TASK_A = "TaskA";
	private static final String TASK_B = "TaskB";
	private static final String TASK_C = "TaskC";
	private static final String TASK_D = "TaskD";
	private static final String TASK_E = "TaskE";
	private static final String TASK_F = "TaskF";
	
	@Test
	@Deployment(resources = { "interruptingVsNotInterrupting/interruptingVsNotInterruptingProcess.bpmn" })
	public void testInterrupting() {
	
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_INTERRUPTING, true);
		runtimeService().startProcessInstanceByKey(PROCESS, variables);
		
		ensureSingleTaskPresent(TASK_B, null, false);
		
		// we have a (timer) job...
		List<Job> jobs = managementService().createJobQuery().list();
		assertEquals(1, jobs.size());
		
		// fire timer
		managementService().executeJob(jobs.get(0).getId());
		
		// we have 'TaskF', but 'TaskB' and 'TaskE' are gone...
		ensureTaskNotPresent(TASK_E);
		ensureTaskNotPresent(TASK_B);
		taskService().complete(ensureSingleTaskPresent(TASK_F, null, false).getId());
		
		// execution is gone
		assertEquals(0, runtimeService().createProcessInstanceQuery().list().size());
	}
	
	@Test
	@Deployment(resources = { "interruptingVsNotInterrupting/interruptingVsNotInterruptingProcess.bpmn" })
	public void testNotInterrupting() {
	
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_INTERRUPTING, false);
		runtimeService().startProcessInstanceByKey(PROCESS, variables);
		
		 ensureSingleTaskPresent(TASK_A, null, false);
		
		// we have a (timer) job...
		List<Job> jobs = managementService().createJobQuery().list();
		assertEquals(1, jobs.size());
		
		// fire timer
		managementService().executeJob(jobs.get(0).getId());
		
		// we still have 'TaskA' and also 'TaskC' and NOT 'TaskD'...
		Task taskA = ensureSingleTaskPresent(TASK_A, null, false);
		ensureSingleTaskPresent(TASK_C, null, false);
		ensureTaskNotPresent(TASK_D);
		
		// execute 'A'
		taskService().complete(taskA.getId());
		
		taskService().complete(ensureSingleTaskPresent(TASK_C, null, false).getId());
		taskService().complete(ensureSingleTaskPresent(TASK_D, null, false).getId());
		
		// execution is gone
		assertEquals(0, runtimeService().createProcessInstanceQuery().list().size());
	}
}