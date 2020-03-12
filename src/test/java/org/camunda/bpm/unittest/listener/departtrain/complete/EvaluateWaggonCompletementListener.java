package org.camunda.bpm.unittest.listener.departtrain.complete;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.unittest.departtrain.businesslogic.WaggonRepairInfo;
import org.camunda.bpm.unittest.departtrain.businesslogic.enumeration.RepairEvaluationResult;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class EvaluateWaggonCompletementListener implements TaskListener {

	@SuppressWarnings("unchecked")
	@Override
	public void notify(DelegateTask delegateTask) {
		RuntimeService runtimeService = delegateTask.getProcessEngine().getRuntimeService();
		WaggonRepairInfo info = (WaggonRepairInfo) runtimeService.getVariable(delegateTask.getExecution().getId(),
				DepartTrainProcessConstants.VAR_ASSUMED_WAGGON);
		RepairEvaluationResult evaluationResult = (RepairEvaluationResult) runtimeService
				.getVariable(delegateTask.getExecution().getId(), DepartTrainProcessConstants.VAR_WAGGON_EVALUATION_RESULT);
		info.setRepairEvaluationResult(evaluationResult);
		switch (evaluationResult) {
		case REPAIR_WAGGON:
			List<WaggonRepairInfo> repairWaggonsList = (List<WaggonRepairInfo>) runtimeService
					.getVariable(delegateTask.getExecution().getId(), DepartTrainProcessConstants.VAR_PROMPT_REPAIR_WAGGONS_LIST);
			if (repairWaggonsList == null) {
				repairWaggonsList = new ArrayList<WaggonRepairInfo>();
			}
			repairWaggonsList.add(info);
			// set to process
			runtimeService.setVariable(delegateTask.getExecution().getId(),
					DepartTrainProcessConstants.VAR_PROMPT_REPAIR_WAGGONS_LIST, repairWaggonsList);
			break;
		case REPLACE_WAGGON:
			List<WaggonRepairInfo> replaceWaggonsList = (List<WaggonRepairInfo>) runtimeService.getVariable(
					delegateTask.getExecution().getId(), DepartTrainProcessConstants.VAR_PROMPT_REPLACE_WAGGONS_LIST);
			if (replaceWaggonsList == null) {
				replaceWaggonsList = new ArrayList<WaggonRepairInfo>();
			}
			replaceWaggonsList.add(info);
			// set to process
			runtimeService.setVariable(delegateTask.getExecution().getId(),
					DepartTrainProcessConstants.VAR_PROMPT_REPLACE_WAGGONS_LIST, replaceWaggonsList);
			break;
		}
	}
}