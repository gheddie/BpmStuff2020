package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class EventSubProcessVerySimpleTest extends BpmTestCase {

	private static final String PROCESS = "eventSubProcessVerySimple";
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	// tasks
	private static final String TASK_REVIEW = "TaskReview";
	private static final String TASK_PROVIDE = "TaskProvide";
	
	// errors
	private static final String ERR_NOT_ENOUGH_DATA = "ERR_NOT_ENOUGH_DATA";
	
	// messages
	private static final String MSG_NOT_ENOUGH_DATA = "MSG_NOT_ENOUGH_DATA";
	
	// variables
	private static final String VAR_ENOUGH_DATA = "enoughData";

	@Test
	@Deployment(resources = { "eventsubprocess/eventSubProcessVerySimple.bpmn" })
	public void testBookingOk() {
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ENOUGH_DATA, false);
		runtimeService().startProcessInstanceByKey(PROCESS, variables);
		
		taskService().complete(ensureSingleTaskPresent(TASK_REVIEW, false).getId());
		
		taskService().complete(ensureSingleTaskPresent(TASK_PROVIDE, false).getId());
		
		// all processes gone
		assertEquals(0, runtimeService().createProcessInstanceQuery().list().size());
	}
}