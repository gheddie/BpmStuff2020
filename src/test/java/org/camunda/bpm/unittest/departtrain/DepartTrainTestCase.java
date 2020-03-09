package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailwayStationBusinessLogicException;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;
import org.junit.Rule;
import org.junit.Test;

public class DepartTrainTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

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
	public void testRepairProcessesForCriticalErrors() {

		RailwayStationBusinessLogic.getInstance().reset();

		// prepare test data
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "TrackExit").withWaggons("Track1", "W1", "W2", "W3", "W4",
				"W5");
		RailwayStationBusinessLogic.getInstance().setDefectCode("W1", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().setDefectCode("W2", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().setDefectCode("W3", WaggonErrorCode.N1);

		// start process A
		startProcess("W1", "W2", "W3", "W4", "W5");

		assertEquals(2, processEngine.getRuntimeService().createProcessInstanceQuery()
				.processDefinitionKey(DepartTrainProcessConstants.PROCESS_REPAIR_FACILITY).list().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testProcessMutlipleCriticalWaggons() {

		RailwayStationBusinessLogic.getInstance().reset();

		// prepare test data
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "TrackExit").withWaggons("Track1", "W1", "W2", "W3", "W4",
				"W5");
		RailwayStationBusinessLogic.getInstance().setDefectCode("W1", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().setDefectCode("W2", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().setDefectCode("W3", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().print(true);
		assertEquals(5, RailwayStationBusinessLogic.getInstance().countWaggons());

		// 3 critical, 1 non critical waggons
		ProcessInstance processInstance = startProcess("W1", "W2", "W3", "W4");

		// we must have 3 repair processes...
		assertEquals(3, processEngine.getRuntimeService().createProcessInstanceQuery()
				.processDefinitionKey(DepartTrainProcessConstants.PROCESS_REPAIR_FACILITY).list().size());

		// 3 waggons must be marked as to be repaired...
		List<String> waggonsToRepair = (List<String>) processEngine.getRuntimeService().getVariable(processInstance.getId(),
				DepartTrainProcessConstants.VAR_WAGGONS_TO_REPAIR);
		assertEquals(3, waggonsToRepair.size());

		// master process is waiting at message catch...
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// 3 tasks 'TASK_ASSUME_REPAIR_TIME'
		List<Task> assumeTasks = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_ASSUME_REPAIR_TIME).list();
		assertEquals(3, assumeTasks.size());

		// process them
		for (Task assumeTask : assumeTasks) {
			processEngine.getTaskService().complete(assumeTask.getId());
		}

		// 3 tasks 'TASK_PROCESS_REPAIR'
		List<Task> repairTasks = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_PROCESS_REPAIR).list();
		assertEquals(3, repairTasks.size());

		// repair waggon 1 of 3 --> not done (back to message catch)
		assertEquals(1, processEngine.getRuntimeService().createEventSubscriptionQuery()
				.eventName(DepartTrainProcessConstants.MSG_WG_REPAIRED).list().size());
		processEngine.getTaskService().complete(repairTasks.get(0).getId());
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// repair waggon 2 of 3 --> not done (back to message catch)
		assertEquals(1, processEngine.getRuntimeService().createEventSubscriptionQuery()
				.eventName(DepartTrainProcessConstants.MSG_WG_REPAIRED).list().size());
		processEngine.getTaskService().complete(repairTasks.get(1).getId());
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// repair waggon 3 of 3 --> done!!!
		assertEquals(1, processEngine.getRuntimeService().createEventSubscriptionQuery()
				.eventName(DepartTrainProcessConstants.MSG_WG_REPAIRED).list().size());
		processEngine.getTaskService().complete(repairTasks.get(2).getId());

		// choose exit track after all repairs...
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.TASK_CHOOSE_EXIT_TRACK);
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testConcurrentDeparture() {

		RailwayStationBusinessLogic.getInstance().reset();

		// prepare test data
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "Track2", "TrackExit").withWaggons("Track1", "W1", "W2");
		RailwayStationBusinessLogic.getInstance().setDefectCode("W1", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().print(true);
		assertEquals(2, RailwayStationBusinessLogic.getInstance().countWaggons());

		// start process A
		ProcessInstance instanceA = startProcess("W1", "W2");

		// start process B
		ProcessInstance instanceB = startProcess("W1", "W2");

		// process B
		assertThat(instanceB).isWaitingAt(DepartTrainProcessConstants.SIG_RO_CANC);

		// process A
		assertThat(instanceA).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// process repair assume for instance A
		List<Task> assumeListA = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_ASSUME_REPAIR_TIME).list();
		assertEquals(1, assumeListA.size());
		processEngine.getTaskService().complete(assumeListA.get(0).getId());
		// process repair for instance A
		List<Task> processRepairListA = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_PROCESS_REPAIR).list();
		assertEquals(1, processRepairListA.size());
		processEngine.getTaskService().complete(processRepairListA.get(0).getId());

		assertThat(instanceA).isWaitingAt(DepartTrainProcessConstants.TASK_CHOOSE_EXIT_TRACK);

		// finish track choosing for A
		processExitTrackChoosing(instanceA);

		// shunt A...
		processShunting(instanceA);

		// finish roll out
		processRollout(instanceA, false);
		assertThat(instanceA).isEnded();

		// B caught signal and must now check its own waggons...
		// assertThat(instanceB).isWaitingAt(DepartTrainProcessConstants.TASK_CHECK_WAGGONS);

		// process assume for instance B
		List<Task> assumeListB = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_ASSUME_REPAIR_TIME).list();
		assertEquals(1, assumeListB.size());
		processEngine.getTaskService().complete(assumeListB.get(0).getId());
		// process repair for instance B
		List<Task> processRepairListB = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_PROCESS_REPAIR).list();
		assertEquals(1, processRepairListB.size());
		processEngine.getTaskService().complete(processRepairListB.get(0).getId());

		// B waiting for exit track
		processExitTrackChoosing(instanceB);

		processShunting(instanceB);

		// B waiting for exit track
		assertThat(instanceB).isWaitingAt(DepartTrainProcessConstants.TASK_CONFIRM_ROLLOUT);

		processRollout(instanceB, true);

		// B is gone...
		assertThat(instanceB).isEnded();

		// waggons must have left the station...
		assertEquals(0, RailwayStationBusinessLogic.getInstance().countWaggons());
	}

	private Map<String, Object> processExitTrackChoosing(ProcessInstance processInstance) {
		Map<String, Object> exitTrackVariables = new HashMap<String, Object>();
		exitTrackVariables.put("exitTrack", "T1");
		processEngine.getTaskService().complete(
				processEngine.getTaskService().createTaskQuery().processInstanceBusinessKey(processInstance.getBusinessKey())
						.taskDefinitionKey(DepartTrainProcessConstants.TASK_CHOOSE_EXIT_TRACK).list().get(0).getId(),
				exitTrackVariables);
		return exitTrackVariables;
	}

	private void processShunting(ProcessInstance instanceA) {
		ensureSingleTaskPresent("TaskShuntWaggons", true);
	}

	private void processRollout(ProcessInstance processInstance, boolean doRollOut) {
		Map<String, Object> rolloutVariables = new HashMap<String, Object>();
		rolloutVariables.put("rolloutConfirmed", doRollOut);
		processEngine.getTaskService().complete(
				ensureSingleTaskPresent(DepartTrainProcessConstants.TASK_CONFIRM_ROLLOUT, processInstance.getBusinessKey(), false)
						.getId(),
				rolloutVariables);
	}

	private ProcessInstance startProcess(String... waggonNumbers) {

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(DepartTrainProcessConstants.VAR_PLANNED_WAGGONS, Arrays.asList(waggonNumbers));
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage(
				DepartTrainProcessConstants.MSG_DEPARTURE_PLANNED,
				RailwayStationBusinessLogic.getInstance().generateBusinessKey(), variables);
		return instance;
	}
}