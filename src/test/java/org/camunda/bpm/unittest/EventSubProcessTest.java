package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class EventSubProcessTest extends BpmTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	// processes
	private static final String PROCESS_EVENT_MAIN = "eventMainProcess";
	
	private static final String PROCESS_EVENT_MAIN_SIMPLIFIED = "eventMainSimplifiedProcess";
	
	private static final String PROCESS_EVENT_FAILURE = "eventFailureSubProcess";
	
	// variables
	private static final String VAR_SEVERE = "varSevere";
	
	public static final String VAR_ERROR_RAISER = "varErrorRaiser";
	
	public static final String VAR_PREREQ_CHECKED = "varPrereqChecked";
	
	// errors
	public static final String ERROR_CHECK = "ERROR_CHECK";
	
	// tasks
	private static final String TASK_START = "TaskStart";
	
	private static final String TASK_FIX_IT = "TaskFixIt";
	
	private static final String TASK_SEVERE_ESCALATION = "TaskSevereEscalation";
	
	private static final String TASK_NOT_SO_SEVERE_ESCALATION = "TaskNotSoSevereEscalation";
	
	private static final String TASK_NORMAL_HANDLING = "TaskNormalHandling";
	
	@Test
	@Deployment(resources = { "eventsubprocess/eventSubProcess.bpmn" })
	public void testEventSubProcessWithoutError() {
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ERROR_RAISER, false);
		runtimeService().startProcessInstanceByKey(PROCESS_EVENT_MAIN, variables);
		
		taskService().complete(ensureSingleTaskPresent(TASK_START, false).getId());
		
		ensureSingleTaskPresent(TASK_NORMAL_HANDLING, false);
	}
	
	// TODO
	// @Test
	@Deployment(resources = { "eventsubprocess/eventSubProcess.bpmn" })
	public void testEventSubProcessWithError() {

		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ERROR_RAISER, true);
		runtimeService().startProcessInstanceByKey(PROCESS_EVENT_MAIN, variables);
		
		// finish check --> it fails
		taskService().complete(ensureSingleTaskPresent(TASK_START, false).getId());
		
		List<ProcessInstance> processList = runtimeService().createProcessInstanceQuery().list();
		
		// we have task 'FixIt'
		ensureSingleTaskPresent(TASK_FIX_IT, false);
	}
	
	@Test
	@Deployment(resources = { "eventsubprocess/eventSubProcessSimplified.bpmn" })
	public void testEventSubProcessSimplifiedWithError() {
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ERROR_RAISER, true);
		runtimeService().startProcessInstanceByKey(PROCESS_EVENT_MAIN_SIMPLIFIED, variables);
	}
}