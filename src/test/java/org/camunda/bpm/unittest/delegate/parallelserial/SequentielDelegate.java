package org.camunda.bpm.unittest.delegate.parallelserial;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import lombok.Data;

@Data
public class SequentielDelegate implements JavaDelegate {
	
	private String sequentielValue;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		System.out.println(" --- SequentielDelegate --- ");
	}
}