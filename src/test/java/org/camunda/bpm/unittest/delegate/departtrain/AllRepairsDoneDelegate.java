package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.constant.ProcessConstants;

public class AllRepairsDoneDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		if (execution.getVariable(ProcessConstants.VAR_REPAIRED_WAGGONS) == null) {
			execution.setVariable(ProcessConstants.VAR_REPAIRED_WAGGONS, new ArrayList<String>());
		}
		List<String> repairedWaggons = (List<String>) execution.getVariable(ProcessConstants.VAR_REPAIRED_WAGGONS);
		repairedWaggons.add((String) execution.getVariable(ProcessConstants.VAR_SINGLE_WAGGON_TO_REPAIR));
		execution.setVariable(ProcessConstants.VAR_ALL_REPAIRS_DONE, repairedWaggons.size() == 1);
	}
}