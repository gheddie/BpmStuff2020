package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.io.Serializable;

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
	
	private String businessKey;

	public static RepairProcessInfo fromValues(String waggonNumber, int assumedRepairDuration, String businessKey) {
		RepairProcessInfo repairProcessInfo = new RepairProcessInfo();
		repairProcessInfo.setWaggonNumber(waggonNumber);
		repairProcessInfo.setAssumedRepairDuration(assumedRepairDuration);
		repairProcessInfo.setBusinessKey(businessKey);
		return repairProcessInfo;
	}
}