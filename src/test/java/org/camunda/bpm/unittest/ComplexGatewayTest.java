package org.camunda.bpm.unittest;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;

public class ComplexGatewayTest {
	
	private static final String PROCESS = "complexgatewayProcess";
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();

	// @Test
	@Deployment(resources = { "complexgateway/complexgatewayProcess.bpmn" })
	public void testComplexGateway() {

		runtimeService().startProcessInstanceByKey(PROCESS);
	}
}