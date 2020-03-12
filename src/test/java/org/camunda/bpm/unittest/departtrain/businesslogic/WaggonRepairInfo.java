package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.io.Serializable;

import org.camunda.bpm.unittest.departtrain.businesslogic.enumeration.RepairEvaluationResult;

import lombok.Data;

@Data
public class WaggonRepairInfo implements Serializable {
	
	private static final long serialVersionUID = -4796291683290246151L;

	private WaggonRepairInfo() {
		// ...
	}

	private String waggonNumber;
	
	// hours
	private int assumedRepairDuration;
	
	// the business key of the repair process
	private String businessKey;
	
	private RepairEvaluationResult repairEvaluationResult;

	public static WaggonRepairInfo fromValues(String waggonNumber, int assumedRepairDuration, String businessKey) {
		WaggonRepairInfo waggonRepairInfo = new WaggonRepairInfo();
		waggonRepairInfo.setWaggonNumber(waggonNumber);
		waggonRepairInfo.setAssumedRepairDuration(assumedRepairDuration);
		waggonRepairInfo.setBusinessKey(businessKey);
		return waggonRepairInfo;
	}
	
	public void setRepairEvaluationResult(RepairEvaluationResult repairEvaluationResult) {
		int werner = 5;
	}
}