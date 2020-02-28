package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import java.util.HashMap;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.delegate.AutomaticReviewDelegate;
import org.junit.Rule;
import org.junit.Test;

public class ReviewTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	private static final String PROCESS_REVIEW = "reviewProcess";

	/**
	 * for loan values up to 2500
	 */
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testPass() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 2000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey(PROCESS_REVIEW, variables);
		checkSingleTaskPresent(AutomaticReviewDelegate.TASK_PASSED_REV);
	}
	
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testStandardReview() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 3000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey(PROCESS_REVIEW, variables);
		checkSingleTaskPresent(AutomaticReviewDelegate.TASK_STD_REV);
	}
	
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testManualReview() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 7000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey(PROCESS_REVIEW, variables);
		checkSingleTaskPresent(AutomaticReviewDelegate.TASK_MAN_REV);
	}
	
	@Test
	@Deployment(resources = { "reviewProcess.bpmn" })
	public void testManualExtendedReview() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(AutomaticReviewDelegate.VAR_AMOUNT, 12000);
		// Given we create a new process instance
		runtimeService().startProcessInstanceByKey(PROCESS_REVIEW, variables);
		checkSingleTaskPresent(AutomaticReviewDelegate.TASK_MAN_EXT_REV);
	}
}