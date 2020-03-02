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

public class EventSubProcessTest extends BpmTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	private static final String PROCESS_EVENT_MAIN = "eventMainProcess";
	
	private static final String PROCESS_EVENT_FAILURE = "eventFailureProcess";
	
	// variables
	private static final String VAR_SEVERE = "varSevere";
	
	public static final String VAR_ERROR_RAISER = "varErrorRaiser";
	
	// errors
	public static final String ERROR_ON_CHECK = "errorOnCheck";
	
	// tasks
	private static final String TASK_START = "TaskStart";
	
	private static final String TASK_FIX_IT = "TaskFixIt";
	
	private static final String TASK_SEVERE_ESCALATION = "TaskSevereEscalation";
	
	private static final String TASK_NOT_SO_SEVERE_ESCALATION = "TaskNotSoSevereEscalation";
	
	private static final String TASK_NORMAL_HANDLING = "TaskNormalHandling";
	
	@Test
	@Deployment(resources = { "eventsubprocess/eventSubProcess.bpmn" })
	public void testEventSubProcess() {

		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ERROR_RAISER, true);
		runtimeService().startProcessInstanceByKey(PROCESS_EVENT_MAIN, variables);
		
		// finish check --> it fails
		taskService().complete(assertSingleTaskPresent(TASK_START).getId());
		
		// do we have a gateway job?
		List<Job> jobs = managementService().createJobQuery().list();
		
		// we have task 'FixIt'
		assertSingleTaskPresent(TASK_FIX_IT);
	}
}