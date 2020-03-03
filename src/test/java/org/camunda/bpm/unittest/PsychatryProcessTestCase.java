package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.HashMap;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class PsychatryProcessTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testAdmissionGranted() {
		runProcess(true);
		ensureSingleTaskPresent("TaskChooseMeal");
	}
	
	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testAdmissionRevoked() {
		runProcess(false);
		ensureSingleTaskPresent("TaskReleasePatient");
	}

	private void runProcess(boolean costTakenOver) {
		runtimeService().startProcessInstanceByMessage("MSG_ADMISSION");
		taskService().complete(ensureSingleTaskPresent("TaskGatherData").getId());
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("costTakenOver", costTakenOver);
		taskService().complete(ensureSingleTaskPresent("TaskReviewData").getId(), variables);
	}
}