package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.List;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class PsychatryProcessTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	// processes
	private static final String PROCESS_PSYCHATRY_EXTERNAL_ADMISSION = "psychatryExternalAdmissionProcess";
	
	// tasks
	private static final String TASK_DATA_AQUISITION = "TaskDataAqusition";
	private static final String TASK_ADVISE_THERAPIST = "TaskAdviseTherapist";
	private static final String TASK_MOO = "TaskMoo";

	// messages
	public static final String MESSAGE_ADMISSION = "MESSAGE_ADMISSION";
	public static final String MESSAGE_OVERTAKE_PATIENT = "MESSAGE_OVERTAKE_PATIENT";
	public static final String MESSAGE_RULE_BRAKE = "MESSAGE_RULE_BRAKE";
	public static final String MESSAGE_CONDITION_CHANGE = "MESSAGE_CONDITION_CHANGE";
	public static final String MSG_TAKEOVER_RESULT = "MSG_TAKEOVER_RESULT";

	//variables
	public static final String VAR_INTERNAL_ADMISSION = "varInternalAdmission";
	public static final String VAR_COST_TAKEN_OVER = "varCostTakenOver";

	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testExternalAdmission() {
		
		// Given we create a new process instance
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_PSYCHATRY_EXTERNAL_ADMISSION);
		
		List<Task> t = taskService().createTaskQuery().list();
		
		List<EventSubscription> events = runtimeService().createEventSubscriptionQuery().list();
		
		taskService().complete(ensureSingleTaskPresent(TASK_DATA_AQUISITION).getId());
		
		taskService().complete(ensureSingleTaskPresent(TASK_MOO).getId());
		
		/*
		ensureVariableSet(VAR_INTERNAL_ADMISSION);
		
		ensureSingleTaskPresent(TASK_ADVISE_THERAPIST);
		*/
		
		ensureSingleTaskPresent(TASK_ADVISE_THERAPIST);
	}
}