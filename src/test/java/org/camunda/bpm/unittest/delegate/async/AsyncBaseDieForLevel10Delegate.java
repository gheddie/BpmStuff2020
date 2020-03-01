package org.camunda.bpm.unittest.delegate.async;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.unittest.delegate.async.base.AsyncBaseDelegate;

public class AsyncBaseDieForLevel10Delegate extends AsyncBaseDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("adding : " + getActivityStamp());
		addValue((List<String>) execution.getVariables().get(VAR_LIST), getActivityStamp());
		if (((int) execution.getVariable(VAR_EXCEPTION_CAUSER)) == 10) {
			die();
		}
	}
	
	@Override
	protected String getActivityStamp() {
		return "Level10";
	}
}