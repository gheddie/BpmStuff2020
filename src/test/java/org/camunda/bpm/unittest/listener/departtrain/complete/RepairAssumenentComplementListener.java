package org.camunda.bpm.unittest.listener.departtrain.complete;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.unittest.departtrain.businesslogic.RailwayStationBusinessLogic;
import org.camunda.bpm.unittest.departtrain.businesslogic.RepairProcessInfo;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;
import org.camunda.bpm.unittest.util.HashBuilder;

public class RepairAssumenentComplementListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String assumedWaggon = (String) delegateTask.getProcessEngine().getRuntimeService()
				.getVariable(delegateTask.getExecutionId(), DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_ASSUME);
		System.out.println("calling back waggon assumement: " + assumedWaggon);
		int singleAssumedTime = (int) delegateTask.getProcessEngine().getRuntimeService()
				.getVariable(delegateTask.getExecutionId(), DepartTrainProcessConstants.VAR_ASSUMED_TIME);
		delegateTask.getProcessEngine().getRuntimeService()
				.correlateMessage(DepartTrainProcessConstants.MSG_REPAIR_ASSUMED,
						(String) delegateTask.getProcessEngine().getRuntimeService().getVariable(delegateTask.getExecutionId(),
								DepartTrainProcessConstants.VAR_DEP_PROC_BK),
						HashBuilder.create()
								.withValuePair(DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_ASSUME,
										RepairProcessInfo.fromValues(assumedWaggon, singleAssumedTime,
												RailwayStationBusinessLogic.getInstance().generateBusinessKey()))
								.build());
	}
}