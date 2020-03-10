package org.camunda.bpm.unittest.delegate.departtrain;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.businesslogic.RepairTimeAssumement;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;
import org.camunda.bpm.unittest.departtrain.util.RailTestUtil;

public class AllAssumementsDoneDelegate implements JavaDelegate {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		if (execution.getVariable(DepartTrainProcessConstants.VAR_ASSUMED_WAGGONS) == null) {
			execution.setVariable(DepartTrainProcessConstants.VAR_ASSUMED_WAGGONS, new ArrayList<String>());
		}
		List<RepairTimeAssumement> assumedWaggons = (List<RepairTimeAssumement>) execution.getVariable(DepartTrainProcessConstants.VAR_ASSUMED_WAGGONS);
		assumedWaggons.add((RepairTimeAssumement) execution.getVariable(DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_ASSUME));
		// all waggons assumed?
		List<String> waggonsToAssume = (List<String>) execution.getVariable(DepartTrainProcessConstants.VAR_WAGGONS_TO_ASSUME);
		boolean allAssumed = RailTestUtil.areListsEqual(convert(assumedWaggons),
				waggonsToAssume);
		execution.setVariable(DepartTrainProcessConstants.VAR_ALL_ASSUMEMENTS_DONE, allAssumed);
	}

	private List<String> convert(List<RepairTimeAssumement> assumedWaggons) {
		List<String> result = new ArrayList<String>();
		for (RepairTimeAssumement assumedWaggon : assumedWaggons) {
			result.add(assumedWaggon.getWaggonNumber());
		}
		return result;
	}
}