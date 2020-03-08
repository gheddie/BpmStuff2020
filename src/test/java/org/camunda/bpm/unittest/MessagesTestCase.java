package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.jobQuery;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.junit.Assert.assertEquals;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;

import java.util.List;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.util.DataGrid;
import org.junit.Rule;
import org.junit.Test;

public class MessagesTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	private static final String PROCESS_MESSAGES = "messagesProcess";
	
	// messages
	private static final String MSG_ONE = "MSG_ONE";
	
	private static final String MSG_TWO = "MSG_TWO";
	
	// timer
	static final String TIMER_ONE = "TimerOne";
	
	// tasks
	private static final String TASK_ONE = "TaskOne";
	
	private static final String TASK_TWO = "TaskTwo";

	private static final String TASK_THREE = "TaskThree";

	@Test
	@Deployment(resources = { "messages/messagesProcess.bpmn" })
	public void testTaskOne() {
	
		runtimeService().startProcessInstanceByKey(PROCESS_MESSAGES);
		
		// post message 'MSG_ONE' --> leads to task definition key 'TaskOne'
		runtimeService().correlateMessage(MSG_ONE);
		ensureSingleTaskPresent(TASK_ONE, false);
	}
	
	@Test
	@Deployment(resources = { "messages/messagesProcess.bpmn" })
	public void testTaskTwo() {
		
		runtimeService().startProcessInstanceByKey(PROCESS_MESSAGES);
		
		// post message 'MSG_ONE' --> leads to task definition key 'TaskOne'
		runtimeService().correlateMessage(MSG_TWO);
		ensureSingleTaskPresent(TASK_TWO, false);
	}
	
	@Test
	@Deployment(resources = { "messages/messagesProcess.bpmn" })
	public void testTimerOne() {
		
		runtimeService().startProcessInstanceByKey(PROCESS_MESSAGES);
		
		List<Job> jobs = jobQuery().list();
		
		assertEquals(1, jobs.size());

		// fire timer 'TimerOne' --> leads to task definition key 'TaskThree'
		managementService().executeJob(jobs.get(0).getId());
		ensureSingleTaskPresent(TASK_THREE, false);
	}
}