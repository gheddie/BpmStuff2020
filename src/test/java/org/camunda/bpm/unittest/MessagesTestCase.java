package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import java.util.List;

import org.camunda.bpm.engine.impl.jobexecutor.JobHandlerConfiguration;
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.jobQuery;

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

	@Test
	@Deployment(resources = { "messagesProcess.bpmn" })
	public void testTaskOne() {
	
		runtimeService().startProcessInstanceByKey(PROCESS_MESSAGES);
		
		// post message 'MSG_ONE' --> leads to task definition key 'TaskOne'
		runtimeService().correlateMessage(MSG_ONE);
		checkSingleTaskPresent(TASK_ONE);
	}
	
	@Test
	@Deployment(resources = { "messagesProcess.bpmn" })
	public void testTaskTwo() {
		
		runtimeService().startProcessInstanceByKey(PROCESS_MESSAGES);
		
		// post message 'MSG_ONE' --> leads to task definition key 'TaskOne'
		runtimeService().correlateMessage(MSG_TWO);
		checkSingleTaskPresent(TASK_TWO);
	}
	
	// @Test
	@Deployment(resources = { "messagesProcess.bpmn" })
	public void testTimerOne() {
		
		runtimeService().startProcessInstanceByKey(PROCESS_MESSAGES);
		
		// fire timer 'TimerOne' --> leads to task definition key 'TaskThree'
		// fireTimer(TIMER_ONE);
		
		// runtimeService().create
		long count = jobQuery().count();
		
		List<Job> jobs = jobQuery().list();
		
		TimerEntity timerEntity = (TimerEntity) jobs.get(0);
		JobHandlerConfiguration poo = timerEntity.getJobHandlerConfiguration();
		
		String moo = timerEntity.getJobDefinitionId();
		
		checkSingleTaskPresent("TaskThree");
	}
}