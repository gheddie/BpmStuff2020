package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessConfig;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessConfigException;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
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

	// variables
	public static final String VAR_ALL_WAGGONS_AVAIABLE = "allWaggonsAvailable";
	public static final String VAR_TRAIN_CONFIG = "trainConfig";
	public static final String VAR_PLANNED_WAGGON_LIST = "plannedWaggonList";

	// links
	private static final String LNK_SUG_REP = "LNK_SUG_REP";
	private static final String LNK_CANC_DO = "LNK_CANC_DO";
	private static final String LNK_REPAIR_WGS = "LNK_REPAIR_WGS";

	// signals
	private static final String SIG_DEP_STAT_CHANGED = "SIG_DEP_STAT_CHANGED";
	private static final String SIG_DEP_ORD_CANC = "SIG_DEP_ORD_CANC";

	// messages
	private static final String MSG_DEPARTMENT_ORDER_CREATED = "MSG_DEPARTMENT_ORDER_CREATED";

	// bpm errors
	public static final String BPM_ERROR_NO_WAGGONS_PLANNED = "BPM_ERROR_NO_WAGGONS_PLANNED";

	/**
	 * awaits a {@link RailwayStationBusinessConfigException} on creating an invalid
	 * {@link RailwayStationBusinessConfig}.
	 */
	@Test(expected = RailwayStationBusinessConfigException.class)
	public void testInvalidRailwayStationBusinessLogic() {
		new RailwayStationBusinessConfig().withTracks("Track1", "Track2").withWaggons("Track3", "W1", "W2");
	}

	/**
	 * Depart order A gets created, B detects '${allWaggonsAvailable == false}'m,
	 * then waits for signal 'SIG_DEP_ORD_CANC' in order to then check waggons
	 * itself an continue.
	 */
	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testCancelDepartOrderAndAwaitSignal() {

		RailwayStationBusinessLogic businessLogic = RailwayStationBusinessLogic.getInstance();
		businessLogic.init(new RailwayStationBusinessConfig().withTracks("Track1", "Track2")
				.withWaggons("Track1", "W1", "W2", "W3").withWaggons("Track2", "W4", "W5", "W6"));
		businessLogic.print();

		cancelDepartOrder();

		List<String> waggonList = createWaggonList(7);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_PLANNED_WAGGON_LIST, waggonList);
		runtimeService().startProcessInstanceByMessage(MSG_DEPARTMENT_ORDER_CREATED, RailwayStationBusinessLogic.getInstance().generateBusinessKey(), variables);
		// we have 7 waggons to check...
		assertEquals(7, taskService().createTaskQuery().taskDefinitionKey(TASK_CHECK_WAGGON).list().size());
		// assertThat(processInstance).hasPassed(TASK_CHECK_SHUNTING_ORDER,
		// END_EVENT_NOMINAL);
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testDeployment() {

	}

	private void cancelDepartOrder() {

		// TODO Auto-generated method stub
	}

	// ---

	private List<String> createWaggonList(int count) {
		List<String> result = new ArrayList<String>();
		for (int index = 0; index < count; index++) {
			result.add("WG_" + index);
		}
		return (List<String>) result;
	}
}