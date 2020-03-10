package org.camunda.bpm.unittest.departtrain.businesslogic.entity;

public abstract class RailTestEntity<T> {
	
	private static final String DIVIDER = "@";

	public abstract T fromString(String value);

	public String getPrimaryValue(String value) {
		return split(value)[0];
	}
	
	public Object getSecondaryValue(String value) {
		return split(value)[1];
	}
	
	private String[] split(String value) {
		return value.split(DIVIDER);
	}
	
	protected boolean hasSecondaryValue(String value) {
		return (value.contains(DIVIDER));
	}
}