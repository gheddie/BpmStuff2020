package org.camunda.bpm.unittest.listener.departtrain.create;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

public class RepairAssumenentCreateListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		int wernert = 5;
	}
}