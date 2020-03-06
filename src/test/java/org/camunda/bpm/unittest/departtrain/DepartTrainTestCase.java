package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
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
	public void testAllWaggonsOk() {

		HashMap<String, Object> waggonList = createWaggonList(7);
		runtimeService().startProcessInstanceByMessage(MSG_DEPARTMENT_ORDER_CREATED, waggonList);
		// we have 7 waggons to check...
		assertEquals(7, taskService().createTaskQuery().taskDefinitionKey(TASK_CHECK_WAGGON).list().size());
		// assertThat(processInstance).hasPassed(TASK_CHECK_SHUNTING_ORDER,
		// END_EVENT_NOMINAL);
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testDeployment() {

	}

	// ---

	private HashMap<String, Object> createWaggonList(int count) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<String> userList = new ArrayList<String>();
		for (int index = 0; index < count; index++) {
			userList.add("waggon_" + index);
		}
		result.put("waggonList", userList);
		return result;
	}
}