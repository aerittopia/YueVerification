package com.aeritt.yue.verification.step;

import com.aeritt.yue.api.discord.DiscordButtonManager;
import com.aeritt.yue.api.util.message.MessageBuilderUtil;
import com.aeritt.yue.verification.api.logic.StepService;
import com.aeritt.yue.verification.api.model.Step;
import com.aeritt.yue.verification.api.model.StepData;
import com.aeritt.yue.verification.config.step.StepConfig;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AcknowledgementStep extends Step {
	private final MessageBuilderUtil messageBuilderUtil;
	private final DiscordButtonManager buttonManager;
	private final StepService stepService;
	private final StepConfig stepConfig;

	public AcknowledgementStep(MessageBuilderUtil messageBuilderUtil, DiscordButtonManager buttonManager,
	                           StepService stepService, StepConfig stepConfig) {
		this.messageBuilderUtil = messageBuilderUtil;
		this.buttonManager = buttonManager;
		this.stepService = stepService;
		this.stepConfig = stepConfig;
	}

	@Override
	public void execute(StepData stepData) {
		MessageEmbed embed = messageBuilderUtil.embed(
				stepConfig.getAcknowledgement().getAcceptationEmbedId(),
				stepData.getUser(),
				Optional.empty()
		);

		List<Button> buttons = List.of(
				messageBuilderUtil.button(stepConfig.getAcknowledgement().getReadButtonId(), stepData.getUser()),
				messageBuilderUtil.button(stepConfig.getAcknowledgement().getAcceptButtonId(), stepData.getUser())
		);
		buttonManager.addButton(buttons.get(0).getId(), this::handleInformationButton);
		buttonManager.addButton(buttons.get(1).getId(), this::handleAcceptButton);


		stepData.setStep(this);
		stepData.getMessage().editMessageEmbeds(embed).setActionRow(buttons).queue();
	}

	private void handleInformationButton(ButtonInteractionEvent event) {
		StepData stepData = stepService.getUser(event.getUser().getId());
		if (stepData.getStep() != this) return;

		stepData.getMessage().delete().queue();

		stepData.getUser().openPrivateChannel().flatMap(
				privateChannel -> privateChannel.sendMessageEmbeds(
						messageBuilderUtil.embed(
								stepConfig.getAcknowledgement().getAcknowledgementEmbedId(),
								stepData.getUser(),
								Optional.empty()

						)
				)
		).queue();

		MessageEmbed embed = messageBuilderUtil.embed(
				stepConfig.getAcknowledgement().getAcceptationEmbedId(),
				stepData.getUser(),
				Optional.empty()
		);

		Button button = messageBuilderUtil.button(stepConfig.getAcknowledgement().getAcceptButtonId(), stepData.getUser());

		stepData.getUser().openPrivateChannel().flatMap(
				privateChannel -> privateChannel.sendMessageEmbeds(embed).setActionRow(button)
		).queue(stepData::setMessage);
	}

	private void handleAcceptButton(ButtonInteractionEvent event) {
		StepData stepData = stepService.getUser(event.getUser().getId());
		if (stepData.getStep() != this) return;

		stepData.setNextAllowed(true);
		stepService.nextStep(stepData);
	}

	@Override
	public String getName() {
		return "acknowledgement";
	}
}
