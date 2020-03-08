package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

import lombok.Data;

@Data
public class Waggon {

	private String waggonNumber;
	
	private WaggonErrorCode waggonErrorCode;

	public boolean isDefect() {
		return (waggonErrorCode != null && waggonErrorCode.isCritical());
	}
}