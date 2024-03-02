package com.aeritt.yue.verification.api.logic;

import com.aeritt.yue.verification.api.model.Step;

import java.util.List;

public interface StepStorage {
	void registerStep(Step step);

	void unregisterStep(Step step);

	Step getStep(String name);

	List<Step> getSteps();
}
