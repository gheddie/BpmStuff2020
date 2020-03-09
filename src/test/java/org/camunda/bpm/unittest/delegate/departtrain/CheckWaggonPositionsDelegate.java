package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class CheckWaggonPositionsDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// exit track must have benn chosen...
		if (execution.getVariable(DepartTrainProcessConstants.VAR_EXIT_TRACK) == null) {
			throw new BpmnError(DepartTrainProcessConstants.ERR_NO_EXIT_TR);
		}
		execution.setVariable(DepartTrainProcessConstants.VAR_POSITIONS_OK, false);
	}
}