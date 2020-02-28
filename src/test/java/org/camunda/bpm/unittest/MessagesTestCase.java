package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
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

	@Test
	@Deployment(resources = { "messagesProcess.bpmn" })
	public void testTaskOne() {
	
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_MESSAGES);
		
		// post message 'MSG_ONE' --> leads to task definition key 'TaskOne'
		
		// post message 'MSG_TWO' --> leads to task definition key 'TaskTwo'
		
		// fire timer 'TimerOne' --> leads to task definition key 'TaskThree'
		fireTimer(TIMER_ONE);
	}
}