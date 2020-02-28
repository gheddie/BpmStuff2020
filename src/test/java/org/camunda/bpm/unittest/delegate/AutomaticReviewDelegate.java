package org.camunda.bpm.unittest.delegate;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class AutomaticReviewDelegate implements JavaDelegate {
	
	// value 5000-9999
	private static final String ERR_ARM = "ERR_ARM";

	// value 10000 and over
	private static final String ERR_ARM_EXT = "ERR_ARM_EXT";

	// process variable
	public static final String VAR_AMOUNT = "amount";
	
	// tasks
	public static final String TASK_MAN_EXT_REV = "TaskManExtReview";
	public static final String TASK_MAN_REV = "TaskManReview";
	public static final String TASK_STD_REV = "TaskStdReview";
	public static final String TASK_PASSED_REV = "TaskPassedReview";

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		Integer amount = (Integer) execution.getVariable(VAR_AMOUNT);
		
		if (amount >= 5000 && amount < 10000) {
			throw new BpmnError(ERR_ARM);
		} else if (amount >= 10000) {
			throw new BpmnError(ERR_ARM_EXT);
		}
	}
}