package org.camunda.bpm.unittest.delegate.departtrain;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CheckExitTrackDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("...CheckExitTrackDelegate...");
	}
}