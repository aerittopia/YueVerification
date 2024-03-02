package com.aeritt.yue.verification.logic;

import com.aeritt.yue.api.service.PersonService;
import com.aeritt.yue.verification.api.model.Step;
import com.aeritt.yue.verification.api.model.StepData;
import com.aeritt.yue.verification.config.SettingsConfig;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StepService implements com.aeritt.yue.verification.api.logic.StepService {
	private final SettingsConfig settingsConfig;
	private final PersonService personService;
	private final StepStorage stepStorage;

	private final Map<String, StepData> unverifiedUsers = new HashMap<>();

	@Autowired
	public StepService(@Qualifier ApplicationContext ctx, SettingsConfig settingsConfig, PersonService personService, StepStorage stepStorage) {
		this.settingsConfig = settingsConfig;
		this.personService = personService;
		this.stepStorage = stepStorage;
	}

	@Override
	public void verifyUser(User user) {
		if (personService.userExists(user.getId()) || (!personService.userExists(user.getId()) && personService.getPersonRoleService().hasRole(user.getId(), settingsConfig.getVerifiedRoleId())))
			return;

		StepData stepData = new StepData(null, true, user, null, null, null);
		unverifiedUsers.put(user.getId(), stepData);

		nextStep(user);
	}

	@Override
	public void nextStep(User user) {
		StepData stepData = unverifiedUsers.get(user.getId());
		if (stepData == null)
			return;

		if (stepData.isNextAllowed()) {
			stepData.setNextAllowed(false);

			if (stepData.getStep() == null) {
				Step step = stepStorage.getSteps().get(0);
				stepData.setStep(step);
				stepData.setNextAllowed(true);
				stepData.getStep().execute(stepData);
			} else {
				Step step = stepStorage.getSteps().get(stepStorage.getSteps().indexOf(stepData.getStep()) + 1);
				if (step != null) {
					stepData.setStep(step);
					stepData.setNextAllowed(true);
					stepData.getStep().execute(stepData);
				} else {
					unverifiedUsers.remove(user.getId());
				}
			}
		}
	}

	@Override
	public void forceNextStep(User user, Step step) {
		StepData stepData = unverifiedUsers.get(user.getId());
		if (stepData == null)
			return;

		stepData.setNextAllowed(false);
		stepData.setStep(step);
		stepData.getStep().execute(stepData);
	}

	@Override
	public StepData getUser(String userId) {
		return unverifiedUsers.get(userId);
	}
}
