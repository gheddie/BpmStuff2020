package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class EscalationTestCase extends BpmTestCase {
	
	private static final String PROCESS_ESCALATION = "escalationProcess";
	
	// tasks
	private static final String TASK_PLACE_ORDER = "TaskXYZ";
	private static final String TASK_RECEIVE_GOODS = "TaskReceiveGoods";
	private static final String TASK_SHIP_ORDER = "TaskShipOrder";
	private static final String TASK_REMOVE_PRODUCT = "TaskRemoveProduct";
	private static final String TASK_INFORM_CUSTOMER = "TaskInformCustomer";
	
	// escalation
	private static final String ESC_MORE_THAN_2_DAYS = "ESC_MORE_THAN_2_DAYS";
	
	// errors
	private static final String ERR_NOT_SHIPABLE = "ERR_NOT_SHIPABLE";
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	@Test
	@Deployment(resources = { "escalation/escalationProcess.bpmn" })
	public void testDieOnLevel10() {
		runtimeService().startProcessInstanceByKey(PROCESS_ESCALATION);
	}
}