package com.aeritt.yue.verification.config.step;

import lombok.Getter;

@Getter
public class StepConfig {
	private MainLanguageStepConfig mainLanguage = new MainLanguageStepConfig();
	private AdditionalLanguageStepConfig additionalLanguage = new AdditionalLanguageStepConfig();
	private AcknowledgementStepConfig acknowledgement = new AcknowledgementStepConfig();
	private WelcomeStepConfig welcome = new WelcomeStepConfig();
}
