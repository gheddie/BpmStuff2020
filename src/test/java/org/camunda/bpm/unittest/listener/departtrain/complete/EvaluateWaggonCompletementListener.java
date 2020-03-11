package org.camunda.bpm.unittest.listener.departtrain.complete;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.unittest.departtrain.businesslogic.RepairProcessInfo;
import org.camunda.bpm.unittest.departtrain.businesslogic.enumeration.RepairEvaluationResult;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class EvaluateWaggonCompletementListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		RuntimeService runtimeService = delegateTask.getProcessEngine().getRuntimeService();
		RepairProcessInfo info = (RepairProcessInfo) runtimeService.getVariable(delegateTask.getExecution().getId(),
				DepartTrainProcessConstants.VAR_ASSUMED_WAGGON);
		RepairEvaluationResult evaluationResult = (RepairEvaluationResult) runtimeService
				.getVariable(delegateTask.getExecution().getId(), DepartTrainProcessConstants.VAR_WAGGON_EVALUATION_RESULT);
		info.setRepairEvaluationResult(evaluationResult);
		// put it back to the process
		runtimeService.setVariable(delegateTask.getExecution().getId(), DepartTrainProcessConstants.VAR_ASSUMED_WAGGON, info);
	}
}