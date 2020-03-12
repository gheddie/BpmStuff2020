package org.camunda.bpm.unittest.listener.departtrain.complete;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.unittest.departtrain.businesslogic.WaggonRepairInfo;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class WaggonEvaluationProcessCompletementListener implements ExecutionListener {

	@SuppressWarnings({ "unchecked" })
	@Override
	public void notify(DelegateExecution execution) throws Exception {
		List<WaggonRepairInfo> assumedWaggons = (List<WaggonRepairInfo>) execution.getProcessEngine().getRuntimeService()
				.getVariable(execution.getId(), DepartTrainProcessConstants.VAR_ASSUMED_WAGGONS);
	}
}