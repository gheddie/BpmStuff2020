package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CheckWaggonPositionsDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// exit track must have benn chosen...
		if (execution.getVariable("exitTrack") == null) {
			throw new BpmnError("ERR_NO_EXIT_TR");
		}
		execution.setVariable("positionsOk", false);
	}
}