package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class CompensationSubProcessTest extends BpmTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	// process
	private static final String PROCESS_COMPENSATION_MAIN = "compensationMainProcess";
	
	// variables
	private static final String VAR_BOOKING_VALID = "bookingValid";
	
	// tasks
	private static final String TASK_BOOK_FLIGHT = "TaskBookFlight";
	private static final String TASK_CANCEL_FLIGHT = "TaskCancelFlight";
	private static final String TASK_BOOK_HOTEL = "TaskBookHotel";
	private static final String TASK_CANCEL_HOTEL = "TaskCancelHotel";
	private static final String TASK_CHECK_BOOKING = "TaskCheckBooking";
	private static final String TASK_UPDATE_CUSTOMER_RECORD = "TaskUpdateCustomerRecord";
	
	@Test
	@Deployment(resources = { "compensationsubprocess/compensationsubprocess.bpmn" })
	public void testBookingOk() {
		
		prepareWork(true);
		
		// no more tasks
		assertEquals(0, runtimeService().createProcessInstanceQuery().list().size());
		
		// no more process instances
		assertEquals(0, taskService().createTaskQuery().list().size());
	}
	
	@Test
	@Deployment(resources = { "compensationsubprocess/compensationsubprocess.bpmn" })
	public void testBookingNotOk() {
		
		prepareWork(false);
		
		// cancel fligt
		 taskService().complete(assertSingleTaskPresent(TASK_CANCEL_FLIGHT).getId());
		
		// cancel hotel
		 taskService().complete(assertSingleTaskPresent(TASK_CANCEL_HOTEL).getId());
		
		// update customers record
		 taskService().complete(assertSingleTaskPresent(TASK_UPDATE_CUSTOMER_RECORD).getId());
	}

	private void prepareWork(boolean bookingValid) {
		
		runtimeService().startProcessInstanceByKey(PROCESS_COMPENSATION_MAIN);
		
		// someone must book hotel and flight...
		taskService().complete(assertSingleTaskPresent(TASK_BOOK_FLIGHT).getId());
		taskService().complete(assertSingleTaskPresent(TASK_BOOK_HOTEL).getId());
		
		// someone must check the booking
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_BOOKING_VALID, bookingValid);
		taskService().complete(assertSingleTaskPresent(TASK_CHECK_BOOKING).getId(), variables);
	}
}