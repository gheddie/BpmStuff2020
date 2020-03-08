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
		List<Task> waggonChecks = processEngine.getTaskService().createTaskQuery().taskDefinitionKey("TaskCheckWaggons").list();
		assertEquals(2, waggonChecks.size());
		// check all waggons
		for (Task task : waggonChecks) {
			processEngine.getTaskService().complete(task.getId());
		}
		assertThat(instanceA).isWaitingAt("TaskChooseExitTrack");
		// finish track choosing
		processEngine.getTaskService()
				.complete(processEngine.getTaskService().createTaskQuery().taskDefinitionKey("TaskChooseExitTrack").list().get(0).getId());
		// finish roll out
		Map<String, Object> rolloutVariables = new HashMap<String, Object>();
		rolloutVariables.put("rolloutConfirmed", false);
		ensureSingleTaskPresent("TaskConfirmRollout", instanceA.getBusinessKey());
		
		// rollout of A was declined, so A must be gone und b mjust be in charge to check waggons...
		// assertThat(instanceA).isEnded();
		// assertThat(instanceA).isWaitingAt("SignalThrowRoCanc");
		
		/*
		processEngine.getTaskService().complete(processEngine.getTaskService().createTaskQuery().taskDefinitionKey("TaskConfirmRollout").list().get(0).getId(),
				rolloutVariables);
		// process is gone...
		assertThat(instanceA).isEnded();
		*/
	}

	private ProcessInstance startProcess(List<String> waggons) {
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("plannedWaggons", waggons);
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_DEPARTURE_PLANNED",
				RailwayStationBusinessLogic.getInstance().generateBusinessKey(), variables);
		return instance;
	}
}