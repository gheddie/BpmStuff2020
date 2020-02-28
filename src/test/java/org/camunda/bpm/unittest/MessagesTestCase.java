package org.camunda.bpm.unittest;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class MessagesTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	@Test
	@Deployment(resources = { "messagesProcess.bpmn" })
	public void testManualReview() {
		
	}
}