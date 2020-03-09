package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.Assert.assertEquals;

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
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;
import org.camunda.bpm.unittest.departtrain.businesslogic.exception.RailwayStationBusinessLogicException;
import org.junit.Rule;
import org.junit.Test;

public class DepartTrainTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	// ##############################################################################
	// ####################################### tasks
	// ##############################################################################
	
	private static final String TASK_CHOOSE_EXIT_TRACK = "TASK_CHOOSE_EXIT_TRACK";
	private static final String TASK_CHECK_WAGGONS = "TASK_CHECK_WAGGONS";
	private static final String TASK_CONFIRM_ROLLOUT = "TASK_CONFIRM_ROLLOUT";
	private static final String TASK_PROCESS_REPAIR = "TASK_PROCESS_REPAIR";
	private static final String TASK_ASSUME_REPAIR_TIME = "TASK_ASSUME_REPAIR_TIME";

	// signals
	private static final String SIG_RO_CANC = "SIG_RO_CANC";

	// ##############################################################################
	// ####################################### variables
	// ##############################################################################
	
	// Gesamtliste, die in den Prozess eingegeben wird
	public static final String VAR_PLANNED_WAGGONS = "VAR_PLANNED_WAGGONS";
	
	// Entscheidet, ob zu 'TaskChooseExitTrack' übergegangen wird ---> 'TaskAllRepairsDone'
	public static final String VAR_ALL_REPAIRS_DONE = "VAR_ALL_REPAIRS_DONE";
	
	// Hier werden in 'TaskAllRepairsDone' alle zurückgemeldeten Reparaturen gespeichert
	// Es wird zu 'TaskChooseExitTrack' weitergegeben, wenn gilt: ('VAR_REPAIRED_WAGGONS' == 'VAR_WAGGONS_TO_REPAIR')
	public static final String VAR_REPAIRED_WAGGONS = "VAR_REPAIRED_WAGGONS";
	
	// Hier wird sich ab 'TaskProcessCriticalErrors' gemerkt, welche Wagen als repariert zurückzumelden sind
	public static final String VAR_WAGGONS_TO_REPAIR = "VAR_WAGGONS_TO_REPAIR";
	
	// Wird durch den Reparatur-Prozess geschleift und auuch von diesem zurückgegeben
	public static final String VAR_SINGLE_WAGGON_TO_REPAIR = "VAR_SINGLE_WAGGON_TO_REPAIR";
	
	// business key of the 'master' process --> passed to repair 
	// process to able to call back to master
	public static final String VAR_DEP_PROC_BK = "VAR_DEP_PROC_BK";

	// ##############################################################################
	// ####################################### meesages
	// ##############################################################################
	
	public static final String MSG_INVOKE_WAG_REP = "MSG_INVOKE_WAG_REP";

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
		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "TrackExit").withWaggons("Track1", "W1", "W2", "W3", "W4", "W5");
		RailwayStationBusinessLogic.getInstance().setDefectCode("W1", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().setDefectCode("W2", WaggonErrorCode.C1);
		RailwayStationBusinessLogic.getInstance().setDefectCode("W3", WaggonErrorCode.N1);

		// start process A
		ProcessInstance instance = startProcess("W1", "W2", "W3", "W4", "W5");

		assertEquals(2, processEngine.getRuntimeService().createProcessInstanceQuery().processDefinitionKey("repairFacilityProcess").list().size());
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
		assertThat(instanceB).isWaitingAt(SIG_RO_CANC);

		// process A
		assertThat(instanceA).isWaitingAt(TASK_CHECK_WAGGONS);

		completeWaggonChecks(instanceA);

		// receive waggon repaired message (A)
		Map<String, Object> variablesRepairedWaggonsA = new HashMap<String, Object>();
		variablesRepairedWaggonsA.put(VAR_SINGLE_WAGGON_TO_REPAIR, "ABC123");
		// process repair assume for instance A
		List<Task> assumeListA = processEngine.getTaskService().createTaskQuery().taskDefinitionKey(TASK_ASSUME_REPAIR_TIME).list();
		assertEquals(1, assumeListA.size());
		processEngine.getTaskService().complete(assumeListA.get(0).getId());
		// process repair for instance A
		List<Task> processRepairListA = processEngine.getTaskService().createTaskQuery().taskDefinitionKey(TASK_PROCESS_REPAIR).list();
		assertEquals(1, processRepairListA.size());
		processEngine.getTaskService().complete(processRepairListA.get(0).getId());

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

		// receive waggon repaired message (B)
		Map<String, Object> variablesRepairedWaggonsB = new HashMap<String, Object>();
		variablesRepairedWaggonsB.put(VAR_SINGLE_WAGGON_TO_REPAIR, "ABC123");
		// process assume for instance B
		List<Task> assumeListB = processEngine.getTaskService().createTaskQuery().taskDefinitionKey(TASK_ASSUME_REPAIR_TIME).list();
		assertEquals(1, assumeListB.size());
		processEngine.getTaskService().complete(assumeListB.get(0).getId());
		// process repair for instance B
		List<Task> processRepairListB = processEngine.getTaskService().createTaskQuery().taskDefinitionKey(TASK_PROCESS_REPAIR).list();
		assertEquals(1, processRepairListB.size());
		processEngine.getTaskService().complete(processRepairListB.get(0).getId());

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
		variables.put(VAR_PLANNED_WAGGONS, Arrays.asList(waggonNumbers));
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_DEPARTURE_PLANNED",
				RailwayStationBusinessLogic.getInstance().generateBusinessKey(), variables);
		return instance;
	}
}