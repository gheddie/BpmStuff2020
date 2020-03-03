package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.List;

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

	// messages
	private static final String MESSAGE_ADMISSION = "MESSAGE_ADMISSION";
	private static final String MESSAGE_OVERTAKE_PATIENT = "MESSAGE_OVERTAKE_PATIENT";
	private static final String MESSAGE_RULE_BRAKE = "MESSAGE_RULE_BRAKE";
	private static final String MESSAGE_CONDITION_CHANGE = "MESSAGE_CONDITION_CHANGE";

	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testExternalAdmission() {
		
		// Given we create a new process instance
		ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_PSYCHATRY_EXTERNAL_ADMISSION);
		
		List<Task> t = taskService().createTaskQuery().list();
		
		assertSingleTaskPresent(TASK_DATA_AQUISITION);
	}
}