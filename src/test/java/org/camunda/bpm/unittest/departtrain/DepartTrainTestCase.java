package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Waggon;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailwayStationBusinessLogicException;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;
import org.camunda.bpm.unittest.util.HashBuilder;
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
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "TrackExit").withWaggons("Track1", "W1@C1", "W2@C1",
				"W3@N1", "W4", "W5");

		// start process A
		startDepartureProcess("W1", "W2", "W3", "W4", "W5");

		assertEquals(2, processEngine.getRuntimeService().createProcessInstanceQuery()
				.processDefinitionKey(DepartTrainProcessConstants.PROCESS_REPAIR_FACILITY).list().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testProcessMultipleCriticalWaggons() {

		RailwayStationBusinessLogic.getInstance().reset();

		// prepare test data
		RailwayStationBusinessLogic.getInstance().withTracks("Track1@true", "TrackExit@true").withWaggons("Track1", "W1@C1",
				"W2@C1#N1", "W3@C1", "W4", "W5@C1");

		RailwayStationBusinessLogic.getInstance().print(true);

		assertEquals(5, RailwayStationBusinessLogic.getInstance().countWaggons());

		// 3 critical, 1 non critical waggons
		ProcessInstance processInstance = startDepartureProcess("W1@C1", "W2@C1", "W3@C1", "W4");

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

		// this is an error --> loop
		processExitTrack(processInstance, "UNKNOWN");
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.TASK_CHOOSE_EXIT_TRACK);

		// again
		processExitTrack(processInstance, "TrackExit");

		// wait for shunting response
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_SH_DONE);
		
		processShunting(processInstance);
		
		processRollout(processInstance, true);
		
		// process finished
		assertThat(processInstance).isEnded();
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testConcurrentDeparture() {

		RailwayStationBusinessLogic.getInstance().reset();

		// prepare test data
		RailwayStationBusinessLogic.getInstance().withTracks("Track1@true", "TrackExit").withWaggons("Track1", "W1@C1", "W2");

		RailwayStationBusinessLogic.getInstance().print(true);

		assertEquals(2, RailwayStationBusinessLogic.getInstance().countWaggons());

		// start process A
		ProcessInstance instanceA = startDepartureProcess("W1", "W2");

		// start process B
		ProcessInstance instanceB = startDepartureProcess("W1", "W2");

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
		processExitTrack(instanceA, "Track1");

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
		processExitTrack(instanceB, "Track1");

		processShunting(instanceB);

		// B waiting for exit track
		assertThat(instanceB).isWaitingAt(DepartTrainProcessConstants.TASK_CONFIRM_ROLLOUT);

		processRollout(instanceB, true);

		// B is gone...
		assertThat(instanceB).isEnded();

		// waggons must have left the station...
		assertEquals(0, RailwayStationBusinessLogic.getInstance().countWaggons());
	}

	private void processExitTrack(ProcessInstance processInstance, String trackNumber) {
		processEngine.getTaskService().complete(
				processEngine.getTaskService().createTaskQuery().processInstanceBusinessKey(processInstance.getBusinessKey())
						.taskDefinitionKey(DepartTrainProcessConstants.TASK_CHOOSE_EXIT_TRACK).list().get(0).getId(),
				HashBuilder.create().withValuePair(DepartTrainProcessConstants.VAR_EXIT_TRACK, trackNumber).build());
	}

	private void processShunting(ProcessInstance instanceA) {
		ensureSingleTaskPresent(DepartTrainProcessConstants.TASK_SHUNT_WAGGONS, true);
	}

	private void processRollout(ProcessInstance processInstance, boolean doRollOut) {
		processEngine.getTaskService().complete(
				ensureSingleTaskPresent(DepartTrainProcessConstants.TASK_CONFIRM_ROLLOUT, processInstance.getBusinessKey(), false)
						.getId(),
				HashBuilder.create().withValuePair(DepartTrainProcessConstants.VAR_ROLLOUT_CONFIRMED, doRollOut).build());
	}

	private ProcessInstance startDepartureProcess(String... waggonNumbers) {
		List<String> extractedWaggonNumbers = Waggon.getWaggonNumbers(waggonNumbers);
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage(
				DepartTrainProcessConstants.MSG_DEPARTURE_PLANNED,
				RailwayStationBusinessLogic.getInstance().generateBusinessKey(), HashBuilder.create()
						.withValuePair(DepartTrainProcessConstants.VAR_PLANNED_WAGGONS, extractedWaggonNumbers).build());
		return instance;
	}
}