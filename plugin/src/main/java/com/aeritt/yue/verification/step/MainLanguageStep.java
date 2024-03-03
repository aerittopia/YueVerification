package com.aeritt.yue.verification.step;

import com.aeritt.yue.api.discord.DiscordButtonManager;
import com.aeritt.yue.api.language.LanguageService;
import com.aeritt.yue.api.service.PersonService;
import com.aeritt.yue.api.util.message.MessageBuilderUtil;
import com.aeritt.yue.verification.api.logic.StepService;
import com.aeritt.yue.verification.api.model.Step;
import com.aeritt.yue.verification.api.model.StepData;
import com.aeritt.yue.verification.config.step.StepConfig;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MainLanguageStep extends Step {
	private final MessageBuilderUtil messageBuilderUtil;
	private final DiscordButtonManager buttonManager;
	private final LanguageService languageService;
	private final PersonService personService;
	private final StepService stepService;
	private final StepConfig stepConfig;

	@Autowired
	public MainLanguageStep(DiscordButtonManager buttonManager, LanguageService languageService,
	                        MessageBuilderUtil messageBuilderUtil, PersonService personService,
	                        StepService stepService, StepConfig stepConfig) {
		this.languageService = languageService;
		this.buttonManager = buttonManager;
		this.messageBuilderUtil = messageBuilderUtil;
		this.personService = personService;
		this.stepService = stepService;
		this.stepConfig = stepConfig;
	}

	@Override
	public void execute(StepData stepData) {
		MessageEmbed embed = messageBuilderUtil.embed(
				stepConfig.getMainLanguage().getEmbedId(),
				stepData.getUser(),
				Optional.empty()
		);

		final List<String> languages = languageService.getTranslations().keySet().stream().sorted().toList();
		List<Button> buttons = languages.stream().map(
				language -> Button.secondary(getName() + "-" + language, Emoji.fromUnicode(
						EmojiParser.parseToUnicode(":flag_" + language + ":")
				))
		).collect(Collectors.toList());
		buttons.forEach(button -> buttonManager.addButton(button.getId(), this::handleLanguageButton));

		Button continueButton = messageBuilderUtil.button(stepConfig.getMainLanguage().getContinueButtonId(), stepData.getUser());
		buttonManager.addButton(continueButton.getId(), this::handleContinueButton);
		buttons.add(continueButton);

		if (stepData.getMainLanguage() == null)
			stepData.setMainLanguage(languages.stream().filter(
					language -> language.equals(languageService.getDefaultLanguage())
			).findFirst().orElseThrow());

		stepData.setStep(this);

		if (!personService.userExists(stepData.getUser().getId())) {
			personService.addUser(
					stepData.getUser().getId(),
					stepData.getUser().getGlobalName(),
					null,
					stepData.getMainLanguage()
			);
		}

		if (stepData.getMessage() != null) {
			stepData.getMessage().editMessageEmbeds(embed).setActionRow(buttons).queue();
			return;
		}

		stepData.getUser().openPrivateChannel()
				.flatMap(privateChannel -> privateChannel.sendMessageEmbeds(embed).setActionRow(buttons))
				.queue(stepData::setMessage);
	}

	private void handleLanguageButton(ButtonInteractionEvent event) {
		StepData stepData = stepService.getUser(event.getUser().getId());
		String buttonId = event.getComponentId();
		if (stepData.getStep() != this) return;

		buttonId = buttonId.replace(getName() + "-", "");

		String finalButtonId = buttonId;
		Optional<String> language = languageService.getTranslations().keySet().stream().filter(
				lang -> lang.equals(finalButtonId)).findFirst();
		if (language.isEmpty()) return;

		stepData.setMainLanguage(language.get());

		personService.getPersonLanguageService().setLanguage(stepData.getUser().getId(), language.get());
		stepService.nextStep(stepData);
	}

	private void handleContinueButton(ButtonInteractionEvent event) {
		StepData stepData = stepService.getUser(event.getUser().getId());
		String buttonId = event.getComponentId();
		if (stepData.getStep() != this) return;
		if (!buttonId.equals(stepConfig.getMainLanguage().getContinueButtonId())) return;

		stepData.setNextAllowed(true);
		stepService.nextStep(stepData);
	}

	@Override
	public String getName() {
		return "mainLanguage";
	}
}
