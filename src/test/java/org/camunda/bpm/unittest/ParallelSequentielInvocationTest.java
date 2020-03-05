package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.unittest.base.BpmTestCase;
import org.junit.Rule;
import org.junit.Test;

public class ParallelSequentielInvocationTest extends BpmTestCase {

	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
	
	private static final String PROCESS = "parallelSequentielInvocationProcess";
	
	// tasks
	private static final String TASK_T1 = "TaskT1";
	private static final String TASK_T2 = "TaskT2";
	private static final String TASK_T3 = "TaskT3";
	
	private static final String TASK_SEQUENTIEL = "TaskSequentiel";
	private static final String TASK_PARALLEL = "TaskParallel";

	// variables
	private static final String VAR_PARALLEL_VALUES = "parallelValues";
	private static final String VAR_SEQUENTIEL_VALUES = "sequentielValues";

	@Test
	@Deployment(resources = { "parallelsequentiel/parallelSequentielInvocationProcess.bpmn" })
	public void testParallelSequentialInvocation() {
		
		HashMap<String, Object> variables = new HashMap<String, Object>();
		Collection<String> parallelList = new ArrayList<String>();
		variables.put(VAR_PARALLEL_VALUES, parallelList);
		Collection<String>  sequentielList = new ArrayList<String>();
		variables.put(VAR_SEQUENTIEL_VALUES, sequentielList);
		runtimeService().startProcessInstanceByKey(PROCESS, variables);
		
		taskService().complete(ensureSingleTaskPresent(TASK_T1).getId());
		
		List<Task> tasks = taskService().createTaskQuery().list();
		
		ensureSingleTaskPresent(TASK_SEQUENTIEL);
		
		/*
		taskService().complete(ensureSingleTaskPresent(TASK_T1).getId());
		taskService().complete(ensureSingleTaskPresent(TASK_T2).getId());
		taskService().complete(ensureSingleTaskPresent(TASK_T3).getId());
		*/
	}
}