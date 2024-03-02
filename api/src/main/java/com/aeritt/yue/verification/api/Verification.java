package com.aeritt.yue.verification.api;

import com.aeritt.yue.verification.api.logic.StepService;
import com.aeritt.yue.verification.api.logic.StepStorage;

public interface Verification {
	StepService getStepService();

	StepStorage getStepStorage();
}
