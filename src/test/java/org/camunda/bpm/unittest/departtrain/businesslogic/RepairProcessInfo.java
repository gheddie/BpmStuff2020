package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.io.Serializable;

import org.camunda.bpm.unittest.departtrain.businesslogic.enumeration.RepairEvaluationResult;

import lombok.Data;

@Data
public class RepairProcessInfo implements Serializable {
	
	private static final long serialVersionUID = -4796291683290246151L;

	private RepairProcessInfo() {
		// ...
	}

	private String waggonNumber;
	
	// hours
	private int assumedRepairDuration;
	
	// the business key of the repair process
	private String businessKey;
	
	private RepairEvaluationResult repairEvaluationResult;

	public static RepairProcessInfo fromValues(String waggonNumber, int assumedRepairDuration, String businessKey) {
		RepairProcessInfo repairProcessInfo = new RepairProcessInfo();
		repairProcessInfo.setWaggonNumber(waggonNumber);
		repairProcessInfo.setAssumedRepairDuration(assumedRepairDuration);
		repairProcessInfo.setBusinessKey(businessKey);
		return repairProcessInfo;
	}
}