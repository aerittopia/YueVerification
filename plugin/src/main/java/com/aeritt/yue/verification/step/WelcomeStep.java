package com.aeritt.yue.verification.step;

import com.aeritt.yue.api.service.PersonRoleService;
import com.aeritt.yue.api.util.message.MessageBuilderUtil;
import com.aeritt.yue.verification.api.logic.StepService;
import com.aeritt.yue.verification.api.model.Step;
import com.aeritt.yue.verification.api.model.StepData;
import com.aeritt.yue.verification.config.SettingsConfig;
import com.aeritt.yue.verification.config.step.StepConfig;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WelcomeStep extends Step {
	private final MessageBuilderUtil messageBuilderUtil;
	private final PersonRoleService personRoleService;
	private final SettingsConfig settingsConfig;
	private final StepService stepService;
	private final StepConfig stepConfig;

	@Autowired
	public WelcomeStep(MessageBuilderUtil messageBuilderUtil, PersonRoleService personRoleService,
	                   SettingsConfig settingsConfig, StepService stepService, StepConfig stepConfig) {
		this.messageBuilderUtil = messageBuilderUtil;
		this.personRoleService = personRoleService;
		this.settingsConfig = settingsConfig;
		this.stepService = stepService;
		this.stepConfig = stepConfig;
	}

	@Override
	public void execute(StepData stepData) {
		MessageEmbed embed = messageBuilderUtil.embed(
				stepConfig.getWelcome().getEmbedId(),
				stepData.getUser(),
				Optional.empty()
		);

		stepData.setStep(this);
		stepData.setNextAllowed(true);
		stepData.getMessage().editMessageEmbeds(embed).queue();

		personRoleService.addRole(stepData.getUser().getId(), settingsConfig.getVerifiedRoleId());

		stepData.getMessage().delete().queue();
		stepData.getUser().openPrivateChannel().flatMap(
				privateChannel -> privateChannel.sendMessageEmbeds(embed)
		).queue(stepData::setMessage);

		stepService.nextStep(stepData.getUser());
	}

	@Override
	public String getName() {
		return "welcome";
	}
}
