package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class PsychatryProcessTestCase extends BpmTestCase {

	private enum EvaluationResult {
		FURTHER_THERAPY,
		CHANGE_STATION,
		OK
	}

	private static final int RULE_BREAK_PROCESS_COUNT = 13	;

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	private static final Random random = new Random();
	
	// messages
	private static final String MSG_ADMISSION = "MSG_ADMISSION";
	private static final String MSG_RULE_BREAK = "MSG_RULE_BREAK";
	private static final String MSG_SEE_THERAPIST = "MSG_SEE_THERAPIST";
	private static final String MSG_THERAPY_DONE = "MSG_THERAPY_DONE";
	
	// tasks
	private static final String TASK_CHOOSE_MEAL = "TaskChooseMeal";
	private static final String TASK_GATHER_DATA = "TaskGatherData";
	private static final String TASK_REVIEW_DATA = "TaskReviewData";
	private static final String TASK_RELEASE_PATIENT = "TaskReleasePatient";
	private static final String TASK_EVALUATE_PATIENT = "TaskEvaluatePatient";
	private static final String TASK_CHOOSE_STATION = "TaskChooseStation";
	private static final String TASK_CALL_APP = "TaskCallApp";
	
	// variables
	private static final String VAR_TH_SESSION_OUTCOME = "sessionOutcome";

	// process
	private static final String PROCESS_IDENTIFER = "ADMISSION_PROCESS";

	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testCylicEvaluation() {
		
		HashMap<String, String> processInstancesToBusinessKeys = runProcesses(true, false, 1);
		
		taskService().complete(ensureSingleTaskPresent(TASK_CHOOSE_MEAL, false).getId());
		
		// break rule in one of the processes --> release patient
		List<String> keySet = new ArrayList<String>(processInstancesToBusinessKeys.keySet());
		String businessKey = processInstancesToBusinessKeys.get(keySet.get(0));
		runtimeService().correlateMessage(MSG_SEE_THERAPIST, businessKey);
		
		finishEvaluation(EvaluationResult.CHANGE_STATION);
		
		// new station must be chosen
		taskService().complete(ensureSingleTaskPresent(TASK_CHOOSE_STATION, false).getId());
		
		// meal must be rechosen
		taskService().complete(ensureSingleTaskPresent(TASK_CHOOSE_MEAL, false).getId());
		
		// another session
		runtimeService().correlateMessage(MSG_SEE_THERAPIST, businessKey);
		
		finishEvaluation(EvaluationResult.OK);
		
		taskService().complete(ensureSingleTaskPresent(TASK_RELEASE_PATIENT, false).getId());
		
		// TODO sub process fires with error end event, but not with message end event?!?
		ensureSingleTaskPresent(TASK_CALL_APP, false);
	}

	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testRuleBreak() {
		
		HashMap<String, String> processInstancesToBusinessKeys = runProcesses(true, false, RULE_BREAK_PROCESS_COUNT);
		
		List<Task> tasksChooseMeal = ensureTaskCountPresent(TASK_CHOOSE_MEAL, RULE_BREAK_PROCESS_COUNT);
		
		// choose meals
		for (int index = 0; index < RULE_BREAK_PROCESS_COUNT; index++) {
			taskService().complete(tasksChooseMeal.get(index).getId());
		}
		
		// break rule in one of the processes --> release patient
		List<String> keySet = new ArrayList<String>(processInstancesToBusinessKeys.keySet());
		String businessKey = processInstancesToBusinessKeys.get(keySet.get(0));
		runtimeService().correlateMessage(MSG_RULE_BREAK, businessKey);
		
		ensureSingleTaskPresent(TASK_RELEASE_PATIENT, false);
	}

	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testMultipleAdmissions() {

		runProcesses(false, false, 3);

		runProcesses(true, false, 5);
		runProcesses(true, true, 2);

		ensureTaskCountPresent(TASK_RELEASE_PATIENT, 3);
		ensureTaskCountPresent(TASK_CHOOSE_MEAL, 5);
		ensureTaskCountPresent(TASK_REVIEW_DATA, 2);
	}

	// key --> process instance id, value --> business key
	private HashMap<String, String> runProcesses(boolean costTakenOver, boolean stopAtReview, int processCount) {
		HashMap<String, String> processInstancesToBusinessKeys = new HashMap<String, String>();
		String businessKey = null;
		for (int count = 0; count < processCount; count++) {
			businessKey = generateBusinessKey();
			ProcessInstance processInstance = runtimeService().startProcessInstanceByMessage(MSG_ADMISSION, businessKey);
			processInstancesToBusinessKeys.put(processInstance.getProcessInstanceId(), businessKey);
			taskService().complete(ensureSingleTaskPresent(TASK_GATHER_DATA, false).getId());
			HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("costTakenOver", costTakenOver);
			if (!(stopAtReview)) {
				taskService().complete(ensureSingleTaskPresent(TASK_REVIEW_DATA, false).getId(), variables);
			}
		}
		return processInstancesToBusinessKeys;
	}

	private String generateBusinessKey() {
		return PROCESS_IDENTIFER + "_" + random.nextInt(1000);
	}
	
	private void finishEvaluation(EvaluationResult evaluationResult) {
		Map<String, Object> var = new HashMap<String, Object>();
		var.put(VAR_TH_SESSION_OUTCOME, evaluationResult.toString());
		taskService().complete(ensureSingleTaskPresent(TASK_EVALUATE_PATIENT, false).getId(), var);
	}
}