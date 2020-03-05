package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskQuery;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class MultiInstanceProcessTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	private static final String PROCESS_DEFINITION_KEY = "multiInstanceProcess";
	
	/**
	 * Just tests if the process definition is deployable.
	 */
	@Test
	@Deployment(resources = "multiinstance/multiInstanceProcess.bpmn")
	public void testParsingAndDeployment() {
		// ...
	}

	@Test
	@Deployment(resources = "multiinstance/multiInstanceProcess.bpmn")
	public void esWerdenMehrereUserTasksErzeugt() {
		
		processEngine.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
				withParameters());
		assertEquals(taskQuery().list().size(), 2);
	}

	@Test
	@Deployment(resources = "multiinstance/multiInstanceProcess.bpmn")
	public void wennBeideUserEinAngebotAbgebenIstDerProzessTerminiert() {
		
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
				withParameters());
		List<Task> tasks = taskQuery().list();
		for (Task task : tasks) {
			taskService().complete(task.getId());
		}
		assertThat(processInstance).hasPassed("EndEvent_1");
		assertThat(processInstance).isEnded();
	}

	@Test
	@Deployment(resources = "multiinstance/multiInstanceProcess.bpmn")
	public void testProcessTerminatedAfterTimeout() {

		restartJobExecutor();
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
				withParameters());
		Calendar jetzt = Calendar.getInstance();
		jetzt.add(Calendar.MINUTE, 25);
		ClockUtil.setCurrentTime(jetzt.getTime());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(processInstance).hasPassed("EndEvent_3");
		assertThat(processInstance).isEnded();

	}

	@Test
	@Deployment(resources = "multi-instance-task.bpmn")
	@Ignore("Dieser Test schlägt fehl, weil das Boundary Event nur einmal feuert, nicht pro User-Task Instanz")
	public void proUserTaskWirdDasZwsichenEventAufgerufen() {

		restartJobExecutor();
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY,
				withParameters());
		Calendar jetzt = Calendar.getInstance();
		jetzt.add(Calendar.MINUTE, 6);
		ClockUtil.setCurrentTime(jetzt.getTime());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(processInstance).hasPassed("ServiceTask_1");
		assertThat(processInstance).isNotEnded();
		assertEquals(processEngine.getHistoryService().createHistoricActivityInstanceQuery().activityId("ServiceTask_1").list()
				.size(), 2);
	}

	private HashMap<String, Object> withParameters() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("assigneeList", Arrays.asList("user1", "user2"));
		return hashMap;
	}

	private void restartJobExecutor() {
		
		JobExecutor jobExecutor = ((ProcessEngineImpl) processEngine.getProcessEngine()).getProcessEngineConfiguration()
				.getJobExecutor();
		shutdownJobExecutor(jobExecutor);
		jobExecutor.setWaitTimeInMillis(500);
		jobExecutor.start();
		// wait until jobExecutor is active
		while (!jobExecutor.isActive()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ClockUtil.reset();
	}

	private void shutdownJobExecutor(JobExecutor jobExecutor) {
		
		if (jobExecutor.isActive()) {
			jobExecutor.shutdown();
			// wait until jobExecutor is inactive
			while (jobExecutor.isActive()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}