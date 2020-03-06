package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

public enum WaggonErrorCode {

	// critical
	C1(true),
	
	// not critical
	N1(false);
	
	private boolean critical;
	
	WaggonErrorCode(boolean aCritical) {
		this.critical = aCritical;
	}
	
	public boolean isCritical() {
		return critical;
	}
}