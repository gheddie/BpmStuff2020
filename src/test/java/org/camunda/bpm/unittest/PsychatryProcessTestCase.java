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
	
	// messages
	private static final String MSG_ADMISSION = "MSG_ADMISSION";
	
	// tasks
	private static final String TASK_CHOOSE_MEAL = "TaskChooseMeal";
	private static final String TASK_GATHER_DATA = "TaskGatherData";
	private static final String TASK_REVIEW_DATA = "TaskReviewData";
	private static final String TASK_RELEASE_PATIENT = "TaskReleasePatient";

	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testMultipleAdmissions() {

		runProcess(false, false, 3);

		runProcess(true, false, 5);
		runProcess(true, true, 2);

		ensureTaskCountPresent(TASK_RELEASE_PATIENT, 3);
		ensureTaskCountPresent(TASK_CHOOSE_MEAL, 5);
		ensureTaskCountPresent(TASK_REVIEW_DATA, 2);
	}

	private void runProcess(boolean costTakenOver, boolean stopAtReview, int processCount) {
		for (int count = 0; count < processCount; count++) {
			runtimeService().startProcessInstanceByMessage(MSG_ADMISSION);
			taskService().complete(ensureSingleTaskPresent(TASK_GATHER_DATA).getId());
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("costTakenOver", costTakenOver);
			if (!(stopAtReview)) {
				taskService().complete(ensureSingleTaskPresent(TASK_REVIEW_DATA).getId(), variables);
			}
		}
	}
}