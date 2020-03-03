package org.camunda.bpm.unittest.delegate.psychatry;

import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.unittest.PsychatryProcessTestCase;

public class CalculateCostTakeOverDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();
		
		List<EventSubscription> list = runtimeService.createEventSubscriptionQuery().list();
		List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery().list();

		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put(PsychatryProcessTestCase.VAR_COST_TAKEN_OVER, true);
		runtimeService
				.correlateMessage(PsychatryProcessTestCase.MSG_TAKEOVER_RESULT, variables);
	}
}