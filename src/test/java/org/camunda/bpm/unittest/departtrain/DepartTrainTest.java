package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class DepartTrainTest extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();
	
	private static final String PROCESS_DEPART_TRAIN = "departTrainProcess";
	
	// end events
	private static final String END_EVENT_NOMINAL = "EndEventNominal";

	// tasks
	private static final String TASK_CHECK_SHUNTING_ORDER = "TaskCheckShuntingOrder";

	// errors
	// ...
	
	// links
	private static final String LNK_SUG_REP = "LNK_SUG_REP";
	private static final String LNK_CANC_DO = "LNK_CANC_DO";
	private static final String LNK_REPAIR_WGS = "LNK_REPAIR_WGS";
	
	
	// variables
	private static final String VAR_TRAIN_CONFIG = "trainConfig";

	// messages
	private static final String MSG_DEPARTMENT_ORDER_CREATED = "MSG_DEPARTMENT_ORDER_CREATED";
	
	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testDeployment() {
		
		/*
		ProcessInstance processInstance = runtimeService().startProcessInstanceByMessage(MSG_DEPARTMENT_ORDER_CREATED);
		assertThat(processInstance).hasPassed(TASK_CHECK_SHUNTING_ORDER, END_EVENT_NOMINAL);
		*/
	}
}