package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskQuery;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class MultiInstanceProcessTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	private static final String PROCESS_DEFINITION_KEY = "multiInstanceProcess";

	// tasks
	private static final String TASK_DO_SOMETHING = "TaskDoSomething";

	// end events
	private static final String END_EVENT_TIMEOUT = "EndEventTimeout";
	private static final String END_EVENT = "EndEvent";

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
	public void testCreateMultipleUserTasks() {

		// one user task 'Task1' is created for every user (with 'assignee' set)...
		HashMap<String, Object> userList = createUserList(5);
		processEngine.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, userList);
		List<Task> taskList = taskQuery().list();
		assertEquals(5, taskList.size());
	}

	@Test
	@Deployment(resources = "multiinstance/multiInstanceProcess.bpmn")
	public void testProcessTerminatedAllUserTasksFinished() {

		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, createUserList(2));
		List<Task> tasks = taskQuery().list();
		for (Task task : tasks) {
			taskService().complete(task.getId());
		}
		// all user tasks completed...process terminates 'normally'
		assertThat(processInstance).hasPassed(END_EVENT);
		assertThat(processInstance).isEnded();
	}

	@Test
	@Deployment(resources = "multiinstance/multiInstanceProcess.bpmn")
	public void testProcessTerminatedAfterTimeout() {

		restartJobExecutor(((ProcessEngineImpl) processEngine.getProcessEngine()).getProcessEngineConfiguration()
				.getJobExecutor());
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, createUserList(2));
		// let 25 minutes pass (longer than the defined timeout)...
		shiftMinutes(25);
		sleep(1000);
		// time out after 20 minutes...so 'InterruptingTimer' must have fired after 25 minutes!!
		// process must be gone as'InterruptingTimer' is interrupting...
		assertThat(processInstance).hasPassed(END_EVENT_TIMEOUT);
		assertThat(processInstance).isEnded();

	}

	@Test
	@Deployment(resources = "multiinstance/multiInstanceProcess.bpmn")
	public void testInvokeIntermediateEventPerUserTask() {

		restartJobExecutor(((ProcessEngineImpl) processEngine.getProcessEngine()).getProcessEngineConfiguration()
				.getJobExecutor());
		
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, createUserList(2));
		// wait 6 minutes...longer than non interrupting time out (wich is 5 minutes)...
		shiftMinutes(6);
		sleep(1000);
		// process has passed 'TASK_DO_SOMETHING'...
		assertThat(processInstance).hasPassed(TASK_DO_SOMETHING);
		assertThat(processInstance).isNotEnded();
		// 'TaskDoSomething' has only been called once (NOT for every created user)...
		assertEquals(processEngine.getHistoryService().createHistoricActivityInstanceQuery()
				.activityId(TASK_DO_SOMETHING).list().size(), 1);
	}
	
	// ---
	
	private HashMap<String, Object> createUserList(int count) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<String> userList = new ArrayList<String>();
		for (int index = 0; index < count; index++) {
			userList.add("user_" + index);
		}
		result.put("assigneeList", userList);
		return result;
	}
}