package org.camunda.bpm.unittest.departtrain.businesslogic;

import java.io.Serializable;

import lombok.Data;

@Data
public class RepairTimeAssumement implements Serializable {
	
	private static final long serialVersionUID = -4796291683290246151L;

	private RepairTimeAssumement() {
		// ...
	}

	private String waggonNumber;
	
	// hours
	private int assumedRepairDuration;

	public static RepairTimeAssumement fromValues(String waggonNumber, int assumedRepairDuration) {
		RepairTimeAssumement repairTimeAssumement = new RepairTimeAssumement();
		repairTimeAssumement.setWaggonNumber(waggonNumber);
		repairTimeAssumement.setAssumedRepairDuration(assumedRepairDuration);
		return repairTimeAssumement;
	}
}