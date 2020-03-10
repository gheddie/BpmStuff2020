package org.camunda.bpm.unittest.base;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.managementService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

public class BpmTestCase {

	protected Task ensureSingleTaskPresent(String taskName, boolean executeTask) {
		return ensureSingleTaskPresent(taskName, null, executeTask);
	}

	protected Task ensureSingleTaskPresent(String taskName, String businessKey, boolean executeTask) {
		List<Task> taskList = null;
		if (businessKey != null) {
			// regard business key in addition...
			taskList = taskService().createTaskQuery().taskDefinitionKey(taskName).processInstanceBusinessKey(businessKey).list();
		} else {
			// do not regard business key...
			taskList = taskService().createTaskQuery().taskDefinitionKey(taskName).list();
		}
		assertEquals(1, taskList.size());
		Task task = taskList.get(0);
		if (executeTask) {
			taskService().complete(task.getId());
		}
		return task;
	}

	protected List<Task> ensureTaskCountPresent(String taskName, int taskCount) {
		List<Task> taskList = taskService().createTaskQuery().taskDefinitionKey(taskName).list();
		assertEquals(taskCount, taskList.size());
		return taskList;
	}

	protected boolean ensureTaskNotPresent(String taskName) {
		return (taskService().createTaskQuery().taskDefinitionKey(taskName).list().size() == 0);
	}

	protected void ensureProcessesRunning(String processDefinitionKey, int count) {
		List<ProcessInstance> processInstancesList = runtimeService().createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey).list();
		assertEquals(count, processInstancesList.size());
	}

	protected void ensureProcessesRunning(int count) {
		List<ProcessInstance> processInstancesList = runtimeService().createProcessInstanceQuery().list();
		assertEquals(count, processInstancesList.size());
	}

	protected void executeSingleTask(String taskDefinitionKey, String businessKey) {
		List<Task> taskList = taskService().createTaskQuery().taskDefinitionKey(taskDefinitionKey)
				.processInstanceBusinessKey(businessKey).list();
		assertEquals(1, taskList.size());
		taskService().complete(taskList.get(0).getId());
	}

	protected void ensureVariableSet(String variableName) {
		assertEquals(1, runtimeService().createVariableInstanceQuery().variableName(variableName).list().size());
	}

	protected int ensureProcessInstanceCount(String processDefinitionKey) {
		return runtimeService().createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list().size();
	}

	protected void debugEngineState() {

		System.out.println("---------------[ENGINE STATE]------------------");

		// runtime
		System.out.println("[executions] ---> " + runtimeService().createExecutionQuery().list().size());
		System.out.println("[incidents] ---> " + runtimeService().createIncidentQuery().list().size());
		System.out.println("[process instances] ---> " + runtimeService().createProcessInstanceQuery().list().size());
		System.out.println("[variable instances] ---> " + runtimeService().createVariableInstanceQuery().list().size());

		// task
		System.out.println("[tasks] ---> " + taskService().createTaskQuery().list().size());

		// management
		System.out.println("[jobs] ---> " + managementService().createJobQuery().list().size());

		System.out.println("-----------------------------------------------");
	}

	protected void shiftMinutes(int minutes) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, minutes);
		ClockUtil.setCurrentTime(now.getTime());
	}

	protected void sleep(long milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void restartJobExecutor(JobExecutor jobExecutor) {

		// shut down job executor...
		if (jobExecutor.isActive()) {
			jobExecutor.shutdown();
			// wait until jobExecutor is inactive
			while (jobExecutor.isActive()) {
				sleep(1000);
			}
		}
		jobExecutor.setWaitTimeInMillis(500);
		// start up job executor...
		jobExecutor.start();
		// wait until jobExecutor is active
		while (!jobExecutor.isActive()) {
			sleep(1000);
		}
		ClockUtil.reset();
	}
}