package org.camunda.bpm.unittest.delegate.async.base;

import java.util.List;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public abstract class AsyncBaseDelegate implements JavaDelegate {
	
	public static final String VAR_EXCEPTION_CAUSER = "EXCEPTION_CAUSER";
	
	public static final String VAR_LIST = "VAR_LIST";

	protected void die() {
		throw new BpmnError("123");
	}
	
	protected abstract String getActivityStamp();
	
	protected void addValue(List<String> processValues, String valueToAdd) {
		processValues.add(valueToAdd);
	}
}