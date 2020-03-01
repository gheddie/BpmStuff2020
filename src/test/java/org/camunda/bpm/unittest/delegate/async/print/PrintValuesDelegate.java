package org.camunda.bpm.unittest.delegate.async.print;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.unittest.delegate.async.base.AsyncBaseDelegate;

public class PrintValuesDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		List<String> list = (List<String>) execution.getVariable(AsyncBaseDelegate.VAR_LIST);
		for (String value : list) {
			System.out.println("i have value : " + value);
		}
	}
}