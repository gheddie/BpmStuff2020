package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogicException;
import org.junit.Rule;
import org.junit.Test;

public class DepartTrainTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	private static final String PROCESS_DEPART_TRAIN = "departTrainProcess";

	// end events
	private static final String END_EVENT_NOMINAL = "EndEventNominal";

	// tasks
	private static final String TASK_CHECK_WAGGON = "TaskCheckWaggon";

	// errors
	// ...

	// exclusice gateways
	private static final String EXGW_ALL_WAGGONS_AVAILABLE = "ExGwAllWaggonsAvailable";
	private static final String EXGW_JOIN_1 = "ExGwJoin1";
	private static final String EXGW_JOIN_2 = "ExGwJoin2";
	private static final String EXGW_JOIN_3 = "ExGwJoin3";

	// signals
	private static final String SIG_CATCH_DEP_ORD_CANC = "SigCatchDepOrdCanc";

	// variables
	public static final String VAR_ALL_WAGGONS_AVAIABLE = "allWaggonsAvailable";
	public static final String VAR_TRAIN_CONFIG = "trainConfig";
	public static final String VAR_PLANNED_WAGGON_LIST = "plannedWaggonList";

	// links
	private static final String LNK_SUG_REP = "LNK_SUG_REP";
	private static final String LNK_CANC_DO = "LNK_CANC_DO";
	private static final String LNK_REPAIR_WGS = "LNK_REPAIR_WGS";

	// messages
	private static final String MSG_DEPARTMENT_ORDER_CREATED = "MSG_DEPARTMENT_ORDER_CREATED";

	// bpm errors
	public static final String BPM_ERROR_NO_WAGGONS_PLANNED = "BPM_ERROR_NO_WAGGONS_PLANNED";

	/**
	 * awaits a {@link RailwayStationBusinessLogicException} on creating an invalid
	 * {@link RailwayStationBusinessConfig}.
	 */
	@Test(expected = RailwayStationBusinessLogicException.class)
	public void testInvalidRailwayStationBusinessLogic() {
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "Track2").withWaggons("Track3", "W1", "W2");
	}

	/**
	 * Depart order A gets created, remains on wait 'Check Waggon', B gets started
	 * and detects '${allWaggonsAvailable == false}'m, then waits for signal
	 * SigCatchDepOrdCanc' in order to then check waggons itself an continue.
	 * 
	 * A ---> [W4][W5] --> TrackExit1 B ---> [W4][W5][W6] --> TrackExit2
	 */
	// @Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testCancelDepartOrderAndAwaitSignal() {

		RailwayStationBusinessLogic businessLogic = RailwayStationBusinessLogic.getInstance();
		businessLogic.withTracks("Track1", "Track2", "TrackExit1", "TrackExit2").withWaggons("Track1", "W1", "W2", "W3").withWaggons("Track2", "W4", "W5",
				"W6");
		businessLogic.print();

		// process A
		ProcessInstance processInstanceOrderCreated = startProcess("W4", "W5");

		// A runs up to checking 2 waggons...
		List<Task> waggonCheckTasks = taskService().createTaskQuery().taskDefinitionKey(TASK_CHECK_WAGGON).processInstanceBusinessKey(processInstanceOrderCreated.getBusinessKey())
				.list();
		assertEquals(2, waggonCheckTasks.size());
		assertThat(processInstanceOrderCreated).hasPassed(EXGW_ALL_WAGGONS_AVAILABLE, EXGW_JOIN_1).isWaitingAt(TASK_CHECK_WAGGON);

		// process B
		ProcessInstance processInstanceWaiting = startProcess("W4", "W5", "W6");

		// B waits at signal 'SIG_CATCH_DEP_ORD_CANC'...
		assertThat(processInstanceWaiting).hasPassed(EXGW_ALL_WAGGONS_AVAILABLE).hasNotPassed(EXGW_JOIN_1).isWaitingAt(SIG_CATCH_DEP_ORD_CANC);
		
		// complete waggon checks for A --> process runs through to 'TaskAdviseTrainDeparture'...
		for (Task waggonCheckTask : waggonCheckTasks) {
			taskService().complete(waggonCheckTask.getId());
		}
		
		assertThat(processInstanceOrderCreated).isWaitingAt("TaskAdviseTrainDeparture");
	}

	private ProcessInstance startProcess(String... waggonList) {

		List<String> plannedWaggons = createWaggonList(waggonList);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_PLANNED_WAGGON_LIST, plannedWaggons);
		String businessKey = RailwayStationBusinessLogic.getInstance().generateBusinessKey();
		ProcessInstance instance = runtimeService().startProcessInstanceByMessage(MSG_DEPARTMENT_ORDER_CREATED, businessKey, variables);
		return instance;
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testDeployment() {

	}

	// ---

	private List<String> createWaggonList(String... plannedWaggonNumbers) {
		List<String> result = new ArrayList<String>();
		for (String w : plannedWaggonNumbers) {
			result.add(w);
		}
		return result;
	}
}