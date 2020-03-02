package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class EscalationTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	private static final String PROCESS_ESCALATION = "escalationProcess";
	
	// tasks
	private static final String TASK_PLACE_ORDER = "TaskPlaceOrder";
	private static final String TASK_RECEIVE_GOODS = "TaskReceiveGoods";
	private static final String TASK_SHIP_ORDER = "TaskShipOrder";
	private static final String TASK_REMOVE_PRODUCT = "TaskRemoveProduct";
	private static final String TASK_INFORM_CUSTOMER = "TaskInformCustomer";
	
	// escalation
	private static final String ESC_MORE_THAN_2_DAYS = "ESC_MORE_THAN_2_DAYS";
	
	// errors
	private static final String ERR_NOT_SHIPABLE = "ERR_NOT_SHIPABLE";
	
	// variables
	private static final String VAR_SHIPPING_DAYS = "shippingDays";

	@Test
	@Deployment(resources = { "escalation/escalationProcess.bpmn" })
	public void testImmediately() {
		
		startOrderProcess(0);
		taskService().complete(assertSingleTaskPresent(TASK_PLACE_ORDER).getId());
		assertSingleTaskPresent(TASK_RECEIVE_GOODS);
		assertTaskNotPresent(TASK_REMOVE_PRODUCT);
		assertTaskNotPresent(TASK_INFORM_CUSTOMER);
	}

	@Test
	@Deployment(resources = { "escalation/escalationProcess.bpmn" })
	public void testMoreThan2Days() {
		
		startOrderProcess(7);
		taskService().complete(assertSingleTaskPresent(TASK_PLACE_ORDER).getId());
		assertSingleTaskPresent(TASK_RECEIVE_GOODS);
		assertSingleTaskPresent(TASK_INFORM_CUSTOMER);
	}
	
	@Test
	@Deployment(resources = { "escalation/escalationProcess.bpmn" })
	public void testUnshipable() {
		
		startOrderProcess(21);
		taskService().complete(assertSingleTaskPresent(TASK_PLACE_ORDER).getId());
		assertTaskNotPresent(TASK_RECEIVE_GOODS);
		assertSingleTaskPresent(TASK_REMOVE_PRODUCT);
	}
	
	// ---
	
	private void startOrderProcess(int shippingDays) {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_SHIPPING_DAYS, shippingDays);
		runtimeService().startProcessInstanceByKey(PROCESS_ESCALATION, variables);
	}
}