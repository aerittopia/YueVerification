package com.aeritt.yue.verification.api.logic;

import com.aeritt.yue.verification.api.model.Step;
import com.aeritt.yue.verification.api.model.StepData;
import net.dv8tion.jda.api.entities.User;

public interface StepService {
	void verifyUser(User user);

	void nextStep(StepData stepData);

	void forceNextStep(User user, Step step);

	StepData getUser(String userId);
}
