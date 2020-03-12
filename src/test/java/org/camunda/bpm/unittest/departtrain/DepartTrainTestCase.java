package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.runtime.VariableInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.WaggonRepairInfo;
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.Waggon;
import org.camunda.bpm.unittest.departtrain.businesslogic.enumeration.RepairEvaluationResult;
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

	@SuppressWarnings("unchecked")
	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testStraightAssumement() {

		RailwayStationBusinessLogic.getInstance().reset();

		// prepare test data
		RailwayStationBusinessLogic.getInstance().withTracks("Track1@true", "TrackExit").withWaggons("Track1", "W1@C1#N1",
				"W2@C1", "W3", "W4", "W5");

		ProcessInstance processInstance = startDepartureProcess(getDefaultPlannedDepartureTime(), "W1", "W2", "W3", "W4");

		// we have two facility processes, so 2 assumement tasks...
		assertEquals(2, ensureProcessInstanceCount(DepartTrainProcessConstants.PROCESS_REPAIR_FACILITY));
		List<Task> assumementTasks = ensureTaskCountPresent(DepartTrainProcessConstants.TASK_ASSUME_REPAIR_TIME, null, 2);

		// assume a waggon (for all 2 waggons)
		processWaggonRepairAssumement(processInstance, assumementTasks.get(0), 11);
		processWaggonRepairAssumement(processInstance, assumementTasks.get(1), 4);

		// 2 evaluations to be done...
		List<Task> evaluationTasks = ensureTaskCountPresent(DepartTrainProcessConstants.TASK_EVALUATE_WAGGON,
				processInstance.getId(), 2);

		// 2 facility processes are waiting at 'CATCH_MSG_START_REPAIR'...
		List<ProcessInstance> facilityProcessesInstances = getProcessesInstances(
				DepartTrainProcessConstants.PROCESS_REPAIR_FACILITY);
		assertEquals(2, facilityProcessesInstances.size());
		assertThat(facilityProcessesInstances.get(0)).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_START_REPAIR);
		assertThat(facilityProcessesInstances.get(1)).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_START_REPAIR);

		processStartWaggonEvaluation(evaluationTasks.get(0), RepairEvaluationResult.REPAIR_WAGGON);
		processStartWaggonEvaluation(evaluationTasks.get(1), RepairEvaluationResult.REPLACE_WAGGON);

		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.TASK_PROMPT_WAGGON_REPAIR,
				DepartTrainProcessConstants.TASK_PROMPT_WAGGON_REPLACEMENT);
		
		// we have one prompt repair task...
		ensureTaskCountPresent(DepartTrainProcessConstants.TASK_PROMPT_WAGGON_REPAIR, processInstance.getId(), 1);
		
		// ...and one prompt replacement task
		ensureTaskCountPresent(DepartTrainProcessConstants.TASK_PROMPT_WAGGON_REPLACEMENT, processInstance.getId(), 1);
	}

	@Test
	@Deployment(resources = { "departtrain/departTrainProcess.bpmn" })
	public void testRepairProcessesForCriticalErrors() {

		RailwayStationBusinessLogic.getInstance().reset();

		// prepare test data
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "TrackExit").withWaggons("Track1", "W1@C1", "W2@C1",
				"W3@N1", "W4", "W5");

		// start process A
		startDepartureProcess(getDefaultPlannedDepartureTime(), "W1", "W2", "W3", "W4", "W5");

		assertEquals(2, ensureProcessInstanceCount(DepartTrainProcessConstants.PROCESS_REPAIR_FACILITY));
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
		ProcessInstance processInstance = startDepartureProcess(getDefaultPlannedDepartureTime(), "W1@C1", "W2@C1", "W3@C1",
				"W4");

		// we must have 3 repair processes...
		assertEquals(3, ensureProcessInstanceCount(DepartTrainProcessConstants.PROCESS_REPAIR_FACILITY));

		// 3 waggons must be marked as to be repaired...
		List<String> waggonsToRepair = (List<String>) processEngine.getRuntimeService().getVariable(processInstance.getId(),
				DepartTrainProcessConstants.VAR_WAGGONS_TO_ASSUME);
		assertEquals(3, waggonsToRepair.size());

		// master process is waiting at message catch...
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// 3 tasks 'TASK_ASSUME_REPAIR_TIME'...
		List<Task> repairTasks = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_ASSUME_REPAIR_TIME).list();
		assertEquals(3, repairTasks.size());

		// assume waggon 1 of 3 --> not done (back to message catch)
		assertEquals(1, processEngine.getRuntimeService().createEventSubscriptionQuery()
				.eventName(DepartTrainProcessConstants.MSG_REPAIR_ASSUMED).list().size());
		processWaggonRepairAssumement(processInstance, repairTasks.get(0), 2);
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// assume waggon 2 of 3 --> not done (back to message catch)
		assertEquals(1, processEngine.getRuntimeService().createEventSubscriptionQuery()
				.eventName(DepartTrainProcessConstants.MSG_REPAIR_ASSUMED).list().size());
		processWaggonRepairAssumement(processInstance, repairTasks.get(1), 2);
		assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// assume waggon 3 of 3 --> done!!!
		assertEquals(1, processEngine.getRuntimeService().createEventSubscriptionQuery()
				.eventName(DepartTrainProcessConstants.MSG_REPAIR_ASSUMED).list().size());
		processWaggonRepairAssumement(processInstance, repairTasks.get(2), 2);

		// ...
		// assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.TASK_EVALUATE_REPAIR);

		/*
		 * // this is an error --> loop processExitTrack(processInstance, "UNKNOWN");
		 * assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.
		 * TASK_CHOOSE_EXIT_TRACK);
		 * 
		 * // again processExitTrack(processInstance, "TrackExit");
		 * 
		 * // wait for shunting response
		 * assertThat(processInstance).isWaitingAt(DepartTrainProcessConstants.
		 * CATCH_MSG_SH_DONE);
		 * 
		 * processShunting(processInstance);
		 * 
		 * processRollout(processInstance, true);
		 * 
		 * // process finished assertThat(processInstance).isEnded();
		 */
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
		ProcessInstance instanceA = startDepartureProcess(getDefaultPlannedDepartureTime(), "W1", "W2");

		// start process B
		ProcessInstance instanceB = startDepartureProcess(getDefaultPlannedDepartureTime(), "W1", "W2");

		// process B
		assertThat(instanceB).isWaitingAt(DepartTrainProcessConstants.SIG_RO_CANC);

		// process A
		assertThat(instanceA).isWaitingAt(DepartTrainProcessConstants.CATCH_MSG_WG_REPAIRED);

		// process repair assume for instance A
		List<Task> assumeListA = processEngine.getTaskService().createTaskQuery()
				.taskDefinitionKey(DepartTrainProcessConstants.TASK_ASSUME_REPAIR_TIME).list();
		assertEquals(1, assumeListA.size());

		processWaggonRepairAssumement(instanceA, assumeListA.get(0), 12);

		// ...
		// assertThat(instanceA).isWaitingAt(DepartTrainProcessConstants.TASK_EVALUATE_REPAIR);

		/*
		 * // finish track choosing for A processExitTrack(instanceA, "Track1");
		 * 
		 * // shunt A... processShunting(instanceA);
		 * 
		 * // finish roll out processRollout(instanceA, false);
		 * assertThat(instanceA).isEnded();
		 * 
		 * // B caught signal and must now check its own waggons... //
		 * assertThat(instanceB).isWaitingAt(DepartTrainProcessConstants.
		 * TASK_CHECK_WAGGONS);
		 * 
		 * // process assume for instance B List<Task> assumeListB =
		 * processEngine.getTaskService().createTaskQuery()
		 * .taskDefinitionKey(DepartTrainProcessConstants.TASK_ASSUME_REPAIR_TIME).list(
		 * ); assertEquals(1, assumeListB.size());
		 * processEngine.getTaskService().complete(assumeListB.get(0).getId());
		 * 
		 * // B waiting for exit track processExitTrack(instanceB, "Track1");
		 * 
		 * processShunting(instanceB);
		 * 
		 * // B waiting for exit track
		 * assertThat(instanceB).isWaitingAt(DepartTrainProcessConstants.
		 * TASK_CONFIRM_ROLLOUT);
		 * 
		 * processRollout(instanceB, true);
		 * 
		 * // B is gone... assertThat(instanceB).isEnded();
		 * 
		 * // waggons must have left the station... assertEquals(0,
		 * RailwayStationBusinessLogic.getInstance().countWaggons());
		 */
	}

	private void processWaggonRepairAssumement(ProcessInstance processInstance, Task assumementTask, int hours) {
		processEngine.getTaskService().complete(assumementTask.getId(),
				HashBuilder.create().withValuePair(DepartTrainProcessConstants.VAR_ASSUMED_TIME, hours).build());
	}

	private void processExitTrack(ProcessInstance processInstance, String trackNumber) {
		processEngine.getTaskService().complete(
				processEngine.getTaskService().createTaskQuery().processInstanceBusinessKey(processInstance.getBusinessKey())
						.taskDefinitionKey(DepartTrainProcessConstants.TASK_CHOOSE_EXIT_TRACK).list().get(0).getId(),
				HashBuilder.create().withValuePair(DepartTrainProcessConstants.VAR_EXIT_TRACK, trackNumber).build());
	}

	private void processShunting(ProcessInstance processInstance) {
		ensureSingleTaskPresent(DepartTrainProcessConstants.TASK_SHUNT_WAGGONS, true);
	}

	private void processRollout(ProcessInstance processInstance, boolean doRollOut) {
		processEngine.getTaskService().complete(
				ensureSingleTaskPresent(DepartTrainProcessConstants.TASK_CONFIRM_ROLLOUT, processInstance.getBusinessKey(), false)
						.getId(),
				HashBuilder.create().withValuePair(DepartTrainProcessConstants.VAR_ROLLOUT_CONFIRMED, doRollOut).build());
	}

	private void processStartWaggonEvaluation(Task startWaggonRepairTask, RepairEvaluationResult repairEvaluationResult) {
		// VAR_SINGLE_ASSUMED_WAGGON
		processEngine.getTaskService().complete(startWaggonRepairTask.getId(), HashBuilder.create()
				.withValuePair(DepartTrainProcessConstants.VAR_WAGGON_EVALUATION_RESULT, repairEvaluationResult).build());
	}

	private LocalDateTime getDefaultPlannedDepartureTime() {
		return LocalDateTime.now().plusHours(24);
	}

	private ProcessInstance startDepartureProcess(LocalDateTime plannedDepartureTime, String... waggonNumbers) {
		List<String> extractedWaggonNumbers = Waggon.getWaggonNumbers(waggonNumbers);
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage(
				DepartTrainProcessConstants.MSG_DEPARTURE_PLANNED,
				RailwayStationBusinessLogic.getInstance().generateBusinessKey(),
				HashBuilder.create().withValuePair(DepartTrainProcessConstants.VAR_PLANNED_WAGGONS, extractedWaggonNumbers)
						.withValuePair(DepartTrainProcessConstants.VAR_PLANNED_DEPARTMENT_DATE, plannedDepartureTime).build());
		return instance;
	}
}