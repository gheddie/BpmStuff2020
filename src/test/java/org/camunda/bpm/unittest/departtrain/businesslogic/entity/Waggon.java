package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Waggon extends RailTestEntity<Waggon> {

	private String waggonNumber;
	
	private WaggonErrorCode waggonErrorCode;
	
	public boolean isDefect() {
		return (waggonErrorCode != null && waggonErrorCode.isCritical());
	}

	@Override
	public Waggon fromString(String value) {
		setWaggonNumber(getPrimaryValue(value));
		if (hasSecondaryValue(value)) {
			setWaggonErrorCode(WaggonErrorCode.valueOf((String) getSecondaryValue(value)));			
		}
		return this;
	}

	public static List<String> getWaggonNumbers(String[] waggonNumbers) {
		ArrayList<String> result = new ArrayList<String>();
		for (String waggonNumber : waggonNumbers) {
			result.add(new Waggon().fromString(waggonNumber).getWaggonNumber());
		}
		return result;
	}
}