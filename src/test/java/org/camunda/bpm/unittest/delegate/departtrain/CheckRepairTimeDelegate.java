package org.camunda.bpm.unittest.delegate.departtrain;

import java.time.LocalDateTime;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;

public class CheckRepairTimeDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime plannedDepartment = (LocalDateTime) execution
				.getVariable(DepartTrainProcessConstants.VAR_PLANNED_DEPARTMENT_DATE);
		int totalAssumedHours = (int) execution.getVariable(DepartTrainProcessConstants.VAR_SUMMED_UP_ASSUMED_HOURS);

		boolean exceeded = now.plusHours(totalAssumedHours).isAfter(plannedDepartment);
		execution.setVariable(DepartTrainProcessConstants.VAR_REPAIR_TIME_EXCEEDED,
				exceeded);
	}
}