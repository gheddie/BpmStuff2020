package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class SignalsTestCase extends BpmTestCase {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	private static final String PROCESS_SIGNALS_MAIN = "signalsMainProcess";
	
	private static final String PROCESS_SIGNALS_SUB = "signalsSubProcess";
	
	private static final String SIG_MAIN_THROW = "SIG_MAIN_THROW";
	
	private static final String SIG_SUB_CATCH = "SIG_SUB_CATCH";

	@Test
	@Deployment(resources = { "signals/signalsMainProcess.bpmn", "signals/signalsSubProcess.bpmn" })
	public void testSignals() {
		
		// start main process
		runtimeService().startProcessInstanceByKey(PROCESS_SIGNALS_MAIN);
		
		// start three sub processes
		ProcessInstance subProcess1 = runtimeService().startProcessInstanceByKey(PROCESS_SIGNALS_SUB);
		ProcessInstance subProcess2 = runtimeService().startProcessInstanceByKey(PROCESS_SIGNALS_SUB);
		ProcessInstance subProcess3 = runtimeService().startProcessInstanceByKey(PROCESS_SIGNALS_SUB);
		
		// 3+1 processes are there...
		assertEquals(4, runtimeService().createProcessInstanceQuery().list().size());
		
		Task taskMainProcess = assertSingleTaskPresent("TaskA");
		
		// execute user task A in main --> signal fires from delegate code
		// TODO: do it from bpm element 'throw signal intermediate event' ?!?
		taskService().complete(taskMainProcess.getId());
		
		// we have 1 'TaskB' in main process...
		assertEquals(1, taskService().createTaskQuery().taskDefinitionKey("TaskB").list().size());
		
		// and 3 'TaskC' in sub processes...
		assertEquals(3, taskService().createTaskQuery().taskDefinitionKey("TaskC").list().size());
		
		debugEngineState();
	}
}