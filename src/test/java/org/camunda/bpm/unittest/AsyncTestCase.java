package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.delegate.async.base.AsyncBaseDelegate;
import org.junit.Rule;

public class AsyncTestCase extends BpmTestCase {
	
	private static final String PROCESS_ASYNC = "asyncTestProcess";
	
	private static final String TASK_XYZ = "TaskXYZ";	
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	// @Test
	@Deployment(resources = { "async/asyncTestProcess.bpmn" })
	public void testAsyncProcessStructure() {
		
		// repositoryService().getProcessModel(PROCESS_ASYNC);
		List<ProcessDefinition> definitons = repositoryService().createProcessDefinitionQuery().processDefinitionKey(PROCESS_ASYNC).list();
		assertEquals(1, definitons.size());
		ProcessDefinition processDefinition = definitons.get(0);
	}
	
	// @Test
	@Deployment(resources = { "async/asyncTestProcess.bpmn" })
	public void testDieOnLevel10() {
		runtimeService().startProcessInstanceByKey(PROCESS_ASYNC, getVariables(10));
		executeTaskXYZ();
	}
	
	// @Test
	@Deployment(resources = { "async/asyncTestProcess.bpmn" })
	public void testDieOnLevel20() {
		runtimeService().startProcessInstanceByKey(PROCESS_ASYNC, getVariables(20));
		executeTaskXYZ();
	}
	
	// @Test
	@Deployment(resources = { "async/asyncTestProcess.bpmn" })
	public void testDieOnLevel30() {
		runtimeService().startProcessInstanceByKey(PROCESS_ASYNC, getVariables(30));
		executeTaskXYZ();
	}
	
	// ---
	
	private void executeTaskXYZ() {
		List<Task> taskList = taskService().createTaskQuery().taskDefinitionKey(TASK_XYZ).list();
		taskService().complete(taskList.get(0).getId());
	}

	private HashMap<String, Object> getVariables(int value) {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AsyncBaseDelegate.VAR_LIST, new ArrayList<String>());
		variables.put(AsyncBaseDelegate.VAR_EXCEPTION_CAUSER, value);
		return variables;
	}
}