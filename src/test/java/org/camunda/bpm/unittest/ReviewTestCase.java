package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.delegate.AutomaticReviewDelegate;
import org.junit.Rule;
import org.junit.Test;

public class ReviewTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	/**
	 * for loan values up to 2500
	 */
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testPass() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 2000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey("reviewProcess", variables);
		List<Task> taskList = taskService().createTaskQuery().list();
		assertEquals(1, taskList.size());
		assertEquals(AutomaticReviewDelegate.TASK_PASSED_REV, taskList.get(0).getTaskDefinitionKey());
	}
	
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testStandardReview() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 3000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey("reviewProcess", variables);
		List<Task> taskList = taskService().createTaskQuery().list();
		assertEquals(1, taskList.size());
		assertEquals(AutomaticReviewDelegate.TASK_STD_REV, taskList.get(0).getTaskDefinitionKey());
	}
	
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testManualReview() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 7000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey("reviewProcess", variables);
		List<Task> taskList = taskService().createTaskQuery().list();
		assertEquals(1, taskList.size());
		assertEquals(AutomaticReviewDelegate.TASK_MAN_REV, taskList.get(0).getTaskDefinitionKey());
	}
	
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testManualExtendedReview() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 12000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey("reviewProcess", variables);
		List<Task> taskList = taskService().createTaskQuery().list();
		assertEquals(1, taskList.size());
		assertEquals(AutomaticReviewDelegate.TASK_MAN_EXT_REV, taskList.get(0).getTaskDefinitionKey());
	}
}