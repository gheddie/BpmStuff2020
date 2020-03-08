package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.DepartTrainTestCase;

public class AllRepairsDoneDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		if (execution.getVariable(DepartTrainTestCase.VAR_REPAIRED_WAGGONS) == null) {
			execution.setVariable(DepartTrainTestCase.VAR_REPAIRED_WAGGONS, new ArrayList<String>());
		}
		List<String> repairedWaggons = (List<String>) execution.getVariable(DepartTrainTestCase.VAR_REPAIRED_WAGGONS);
		repairedWaggons.add((String) execution.getVariable(DepartTrainTestCase.VAR_SINGLE_REPAIRED_WAGGON));
		execution.setVariable(DepartTrainTestCase.VAR_REPAIRED_WAGGONS, repairedWaggons);
		execution.setVariable(DepartTrainTestCase.VAR_ALL_REPAIRS_DONE, repairedWaggons.size() == 3);
	}
}