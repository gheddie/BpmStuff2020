package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class OrderShuntingDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// start shunting process
		execution.getProcessEngine().getRuntimeService().startProcessInstanceByMessage("MSG_SH_ORD");
	}
}