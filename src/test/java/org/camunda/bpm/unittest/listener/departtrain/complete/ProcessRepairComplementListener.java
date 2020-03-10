package org.camunda.bpm.unittest.listener.departtrain.complete;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.unittest.departtrain.constant.DepartTrainProcessConstants;
import org.camunda.bpm.unittest.util.HashBuilder;

public class ProcessRepairComplementListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		String repairedWaggon = (String) delegateTask.getProcessEngine().getRuntimeService().getVariable(delegateTask.getExecutionId(),
				DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_REPAIR);
		System.out.println("calling back waggon repair: " + repairedWaggon);
		delegateTask.getProcessEngine().getRuntimeService().correlateMessage(DepartTrainProcessConstants.MSG_WG_REPAIRED,
				(String) delegateTask.getProcessEngine().getRuntimeService().getVariable(delegateTask.getExecutionId(),
						DepartTrainProcessConstants.VAR_DEP_PROC_BK),
				HashBuilder.create()
						.withValuePair(DepartTrainProcessConstants.VAR_SINGLE_WAGGON_TO_REPAIR,
								repairedWaggon)
						.build());
	}
}