package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.List;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class PsychatryProcessTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	@Test
	@Deployment(resources = { "psychatry/psychatryProcess.bpmn" })
	public void testExternalAdmission() {
		
		runtimeService().startProcessInstanceByMessage("MSG_ADMISSION");
		
		taskService().complete(ensureSingleTaskPresent("TaskGatherData").getId());
		
		taskService().complete(ensureSingleTaskPresent("TaskReviewData").getId());
		
		List<Task> tasks = taskService().createTaskQuery().list();
		
		ensureSingleTaskPresent("TaskChooseMeal");
		ensureSingleTaskPresent("TaskReleasePatient");
	}
}