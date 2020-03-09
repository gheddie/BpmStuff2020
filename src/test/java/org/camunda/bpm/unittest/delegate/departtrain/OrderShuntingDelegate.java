package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class OrderShuntingDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// start shunting process
		execution.getProcessEngine().getRuntimeService().startProcessInstanceByMessage(DepartTrainProcessConstants.MSG_SH_ORD);
	}
}