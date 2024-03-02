package com.aeritt.yue.verification.logic;

import com.aeritt.yue.verification.api.model.Step;
import com.aeritt.yue.verification.config.SettingsConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StepStorage implements com.aeritt.yue.verification.api.logic.StepStorage {
	private final SettingsConfig settingsConfig;

	@Getter
	private final List<Step> steps = new ArrayList<>();

	@Autowired
	public StepStorage(SettingsConfig settingsConfig) {
		this.settingsConfig = settingsConfig;
	}

	@Override
	public void registerStep(Step step) {
		steps.add(step);

		steps.sort((o1, o2) -> {
			int o1Order = settingsConfig.getStepOrder().indexOf(o1.getName());
			int o2Order = settingsConfig.getStepOrder().indexOf(o2.getName());

			if (o1Order == o2Order) return 0;
			return o1Order > o2Order ? 1 : -1;
		});
	}

	@Override
	public void unregisterStep(Step step) {
		steps.remove(step);
	}

	@Override
	public Step getStep(String name) {
		return steps.stream().filter(step -> step.getName().equals(name)).findFirst().orElse(null);
	}
}