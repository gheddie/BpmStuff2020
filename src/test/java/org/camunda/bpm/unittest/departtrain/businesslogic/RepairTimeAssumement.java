package org.camunda.bpm.unittest.departtrain.businesslogic;

import lombok.Data;

@Data
public class RepairTimeAssumement {

	private String waggonNumber;
	
	private int assumendHours;
}