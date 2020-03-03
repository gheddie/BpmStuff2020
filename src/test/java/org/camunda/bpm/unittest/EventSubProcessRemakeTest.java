package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class EventSubProcessRemakeTest extends BpmTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	// process
	private static final String PROCESS_SUBPROCESS_EVENT_REMAKE = "eventSubProcessRemake";

	// variables
	private static final String VAR_ENOUGH_DATA = "enoughData";
	
	// tasks
	private static final String TASK_REVIEW = "TaskReview";
	
	private static final String TASK_PROVIDE = "TaskProvide";

	@Test
	@Deployment(resources = { "eventsubprocessremake/eventSubProcessRemake.bpmn" })
	public void testEventSubProcess() {

		runtimeService().startProcessInstanceByKey(PROCESS_SUBPROCESS_EVENT_REMAKE);
		
		ensureProcessesRunning("eventSubProcessRemake", 1);
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(VAR_ENOUGH_DATA, false);
		taskService().complete(ensureSingleTaskPresent(TASK_REVIEW).getId(), variables);
		
		ensureSingleTaskPresent(TASK_PROVIDE);
	}
}