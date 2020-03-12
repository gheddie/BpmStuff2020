package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
		
		debugEngineState();
		
		// cancel fligt
		 taskService().complete(ensureSingleTaskPresent(TASK_CANCEL_FLIGHT, null, false).getId());
		
		// cancel hotel
		 taskService().complete(ensureSingleTaskPresent(TASK_CANCEL_HOTEL, null, false).getId());
		
		// update customers record
		 taskService().complete(ensureSingleTaskPresent(TASK_UPDATE_CUSTOMER_RECORD, null, false).getId());
	}

	private void prepareWork(boolean bookingValid) {
		
		runtimeService().startProcessInstanceByKey(PROCESS_COMPENSATION_MAIN);
		
		// someone must book hotel and flight...
		taskService().complete(ensureSingleTaskPresent(TASK_BOOK_FLIGHT, null, false).getId());
		taskService().complete(ensureSingleTaskPresent(TASK_BOOK_HOTEL, null, false).getId());
		
		// someone must check the booking
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_BOOKING_VALID, bookingValid);
		taskService().complete(ensureSingleTaskPresent(TASK_CHECK_BOOKING, null, false).getId(), variables);
	}
}