package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogicException;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;
import org.junit.Rule;
import org.junit.Test;

public class DepartTrainTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	// tasks
	private static final String TASK_CHOOSE_EXIT_TRACK = "TaskChooseExitTrack";
	private static final String TASK_CHECK_WAGGONS = "TaskCheckWaggons";
	private static final String TASK_CONFIRM_ROLLOUT = "TaskConfirmRollout";
	
	// signals
	private static final String SIGNAL_CATCH_RO_CANC = "SignalCatchRoCanc";

	/**
	 * awaits a {@link RailwayStationBusinessLogicException} on creating an invalid
	 * {@link RailwayStationBusinessConfig}.
	 */
	@Test(expected = RailwayStationBusinessLogicException.class)
	public void testInvalidRailwayStationBusinessLogic() {
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "Track2").withWaggons("Track3", "W1", "W2");
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testDeployment() {
		// ...
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testConcurrentDeparture() {

		// prepare test data
		prepareTestData();

		// start process A
		ProcessInstance instanceA = startProcess("W1", "W2");

		// start process B
		ProcessInstance instanceB = startProcess("W1", "W2");

		// process B
		assertThat(instanceB).isWaitingAt(SIGNAL_CATCH_RO_CANC);

		// process A
		assertThat(instanceA).isWaitingAt(TASK_CHECK_WAGGONS);
		
		completeWaggonChecks(instanceA);
		
		assertThat(instanceA).isWaitingAt(TASK_CHOOSE_EXIT_TRACK);
		
		// finish track choosing for A
		processExitTrackChoosing(instanceA);

		// shunt A...
		processShunting(instanceA);

		// finish roll out
		processRollout(instanceA, false);
		assertThat(instanceA).isEnded();

		// B caught signal and must now check its own waggons...
		assertThat(instanceB).isWaitingAt(TASK_CHECK_WAGGONS);

		// complete checks for B...
		completeWaggonChecks(instanceB);

		// B waiting for exit track
		processExitTrackChoosing(instanceB);

		processShunting(instanceB);

		// B waiting for exit track
		assertThat(instanceB).isWaitingAt(TASK_CONFIRM_ROLLOUT);

		processRollout(instanceB, true);

		// B is gone...
		assertThat(instanceB).isEnded();

		// waggons must have left the station...
		assertEquals(0, RailwayStationBusinessLogic.getInstance().countWaggons());
	}

	private void prepareTestData() {
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "Track2", "TrackExit").withWaggons("Track1", "W1", "W2");
		RailwayStationBusinessLogic.getInstance().setDefectCode("W1", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().print(true);
		assertEquals(2, RailwayStationBusinessLogic.getInstance().countWaggons());
	}

	private void completeWaggonChecks(ProcessInstance processInstance) {
		
		for (Task waggonCheckTask : processEngine.getTaskService().createTaskQuery().taskDefinitionKey(TASK_CHECK_WAGGONS)
				.processInstanceBusinessKey(processInstance.getBusinessKey()).list()) {
			processEngine.getTaskService().complete(waggonCheckTask.getId());
		}
	}

	private Map<String, Object> processExitTrackChoosing(ProcessInstance processInstance) {
		Map<String, Object> exitTrackVariables = new HashMap<String, Object>();
		exitTrackVariables.put("exitTrack", "T1");
		processEngine.getTaskService().complete(processEngine.getTaskService().createTaskQuery().processInstanceBusinessKey(processInstance.getBusinessKey())
				.taskDefinitionKey(TASK_CHOOSE_EXIT_TRACK).list().get(0).getId(), exitTrackVariables);
		return exitTrackVariables;
	}

	private void processShunting(ProcessInstance instanceA) {
		ensureSingleTaskPresent("TaskShuntWaggons", true);
	}

	private void processRollout(ProcessInstance processInstance, boolean doRollOut) {
		Map<String, Object> rolloutVariables = new HashMap<String, Object>();
		rolloutVariables.put("rolloutConfirmed", doRollOut);
		processEngine.getTaskService().complete(ensureSingleTaskPresent(TASK_CONFIRM_ROLLOUT, processInstance.getBusinessKey(), false).getId(),
				rolloutVariables);
	}

	private ProcessInstance startProcess(String... waggonNumbers) {

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("plannedWaggons", Arrays.asList(waggonNumbers));
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_DEPARTURE_PLANNED",
				RailwayStationBusinessLogic.getInstance().generateBusinessKey(), variables);
		return instance;
	}
}