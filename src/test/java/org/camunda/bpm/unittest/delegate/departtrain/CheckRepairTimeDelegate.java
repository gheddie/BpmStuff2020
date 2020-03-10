package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class CheckRepairTimeDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO never exceeding repair time...
		execution.setVariable(DepartTrainProcessConstants.VAR_REPAIR_TIME_EXCEEDED, false);
	}
}