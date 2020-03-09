package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;
import org.camunda.bpm.unittest.departtrain.util.RailTestUtil;

public class AllRepairsDoneDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		if (execution.getVariable(DepartTrainProcessConstants.VAR_REPAIRED_WAGGONS) == null) {
			execution.setVariable(DepartTrainProcessConstants.VAR_REPAIRED_WAGGONS, new ArrayList<String>());
		}
		List<String> repairedWaggons = (List<String>) execution.getVariable(DepartTrainProcessConstants.VAR_REPAIRED_WAGGONS);
		repairedWaggons.add((String) execution.getVariable(DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_REPAIR));
		// all waggons repaired?
		boolean allRepaired = RailTestUtil.areListsEqual(repairedWaggons,
				(List<String>) execution.getVariable(DepartTrainProcessConstants.VAR_WAGGONS_TO_REPAIR));
		execution.setVariable(DepartTrainProcessConstants.VAR_ALL_REPAIRS_DONE, allRepaired);
	}
}