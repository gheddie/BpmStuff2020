package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class MisteriousMultiBehaviourTestCase extends BpmTestCase {

	@Rule
	public ProcessEngineRule processEngine = new ProcessEngineRule();

	@Test
	@Deployment(resources = { "misteriousmultibehaviour/misteriousMultiBehaviourProcess.bpmn" })
	public void testStuff() {

		HashMap<String, Object> variables = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		variables.put("valueList", list);
		String bk = "bk";
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByMessage("MSG_START", bk, variables);

		List<Task> taskList = processEngine.getTaskService().createTaskQuery().taskDefinitionKey("TaskMeh").list();
		assertEquals(3, taskList.size());

		for (Task t : taskList) {
			taskService().complete(t.getId());
		}

		assertThat(instance).isEnded();
	}
}