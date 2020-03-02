package org.camunda.bpm.unittest;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.task.Task;

public class ServiceErrorHandlingTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	// process
	private static final String PROCESS_SERVICE_ERROR_HANDLING = "serviceErrorHandlingProcess";
	
	// tasks
	private static final String TASK_MOO_1 = "TaskMoo1";
	
	private static final String TASK_MOO_2 = "TaskMoo2";
	
	// error
	public static final String ERROR_THE_ONLY = "ERROR_THE_ONLY";
	
	// variables
	public static final String VAR_ERROR_RAISER = "varErrorRaiser";
	
	@Test
	@Deployment(resources = { "serviceErrorHandling/serviceErrorHandling.bpmn" })
	public void testErrorRaisedFromServiceDelegate() {
	
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ERROR_RAISER, true);
		
		runtimeService().startProcessInstanceByKey(PROCESS_SERVICE_ERROR_HANDLING, variables);
		
		assertSingleTaskPresent(TASK_MOO_2);
	}
	
	@Test
	@Deployment(resources = { "serviceErrorHandling/serviceErrorHandling.bpmn" })
	public void testErrorNotRaisedFromServiceDelegate() {
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ERROR_RAISER, false);
		
		runtimeService().startProcessInstanceByKey(PROCESS_SERVICE_ERROR_HANDLING, variables);
		
		assertSingleTaskPresent(TASK_MOO_1);
	}
}