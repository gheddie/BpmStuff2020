package org.camunda.bpm.unittest.departtrain;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
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
import org.camunda.bpm.unittest.departtrain.businesslogic.entity.WaggonErrorCode;
import org.junit.Rule;
import org.junit.Test;

public class DepartTrainTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	private static final String PROCESS_DEPART_TRAIN = "departTrainProcess";

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
	public void testSimpleDeparture() {

		RailwayStationBusinessLogic.getInstance().withTracks("Track1", "Track2", "TrackExit").withWaggons("Track1", "W1", "W2");
		
		RailwayStationBusinessLogic.getInstance().setDefectCode("W1", WaggonErrorCode.C1);
		
		RailwayStationBusinessLogic.getInstance().print(true);
		
		assertEquals(2, RailwayStationBusinessLogic.getInstance().countWaggons());

		List<String> listTaskA = new ArrayList<String>();
		listTaskA.add("W1");
		listTaskA.add("W2");
		ProcessInstance instanceA = startProcess(listTaskA);

		List<String> listTaskB = new ArrayList<String>();
		listTaskB.add("W1");
		listTaskB.add("W2");
		ProcessInstance instanceB = startProcess(listTaskB);

		// process B
		assertThat(instanceB).isWaitingAt("SignalCatchRoCanc");

		// process A
		assertThat(instanceA).isWaitingAt("TaskCheckWaggons");
		List<Task> waggonChecksA = processEngine.getTaskService().createTaskQuery().taskDefinitionKey("TaskCheckWaggons").list();
		assertEquals(2, waggonChecksA.size());
		// check all waggons
		for (Task task : waggonChecksA) {
			processEngine.getTaskService().complete(task.getId());
		}
		assertThat(instanceA).isWaitingAt("TaskChooseExitTrack");
		// finish track choosing
		processEngine.getTaskService()
				.complete(processEngine.getTaskService().createTaskQuery().taskDefinitionKey("TaskChooseExitTrack").list().get(0).getId());
		// finish roll out
		processRollout(instanceA, false);
		assertThat(instanceA).isEnded();

		// B caught signal and must now check its own waggons...
		assertThat(instanceB).isWaitingAt("TaskCheckWaggons");

		// complete checks for B...
		for (Task taskB : processEngine.getTaskService().createTaskQuery().taskDefinitionKey("TaskCheckWaggons")
				.processInstanceBusinessKey(instanceB.getBusinessKey()).list()) {
			processEngine.getTaskService().complete(taskB.getId());
		}

		// B waiting for exit track
		ensureSingleTaskPresent("TaskChooseExitTrack", true);

		// B waiting for exit track
		assertThat(instanceB).isWaitingAt("TaskConfirmRollout");

		processRollout(instanceB, true);

		// B is gone...
		assertThat(instanceB).isEnded();
		
		// waggons must have left the station...
		assertEquals(0, RailwayStationBusinessLogic.getInstance().countWaggons());
	}

	private void processRollout(ProcessInstance processInstance, boolean doRollOut) {
		Map<String, Object> rolloutVariables = new HashMap<String, Object>();
		rolloutVariables.put("rolloutConfirmed", doRollOut);
		processEngine.getTaskService().complete(ensureSingleTaskPresent("TaskConfirmRollout", processInstance.getBusinessKey(), false).getId(),
				rolloutVariables);
	}

	private ProcessInstance startProcess(List<String> waggons) {

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("plannedWaggons", waggons);
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_DEPARTURE_PLANNED",
				RailwayStationBusinessLogic.getInstance().generateBusinessKey(), variables);
		return instance;
	}
}