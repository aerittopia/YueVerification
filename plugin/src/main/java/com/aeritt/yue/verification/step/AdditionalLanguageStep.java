package com.aeritt.yue.verification.step;

import com.aeritt.yue.api.discord.DiscordButtonManager;
import com.aeritt.yue.api.language.LanguageService;
import com.aeritt.yue.api.service.PersonLanguageService;
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
public class AdditionalLanguageStep extends Step {
	private final PersonLanguageService personLanguageService;
	private final MessageBuilderUtil messageBuilderUtil;
	private final DiscordButtonManager buttonManager;
	private final LanguageService languageService;
	private final StepService stepService;
	private final StepConfig stepConfig;

	@Autowired
	public AdditionalLanguageStep(PersonLanguageService personLanguageService, MessageBuilderUtil messageBuilderUtil,
	                              DiscordButtonManager buttonManager, LanguageService languageService,
	                              StepService stepService, StepConfig stepConfig) {
		this.personLanguageService = personLanguageService;
		this.messageBuilderUtil = messageBuilderUtil;
		this.buttonManager = buttonManager;
		this.languageService = languageService;
		this.stepService = stepService;
		this.stepConfig = stepConfig;
	}

	@Override
	public void execute(StepData stepData) {
		MessageEmbed embed = messageBuilderUtil.embed(
				stepConfig.getAdditionalLanguage().getEmbedId(),
				stepData.getUser(),
				Optional.empty()
		);

		final List<String> languages = languageService.getTranslations().keySet().stream().sorted().collect(Collectors.toList());
		languages.remove(stepData.getMainLanguage());
		languages.removeAll(stepData.getAdditionalLanguages());

		List<Button> buttons = languages.stream().map(
				language -> Button.secondary(getName() + "-" + language, Emoji.fromUnicode(
						EmojiParser.parseToUnicode(":flag_" + language + ":")
				))
		).collect(Collectors.toList());

		buttons.add(
				messageBuilderUtil.button(stepConfig.getAdditionalLanguage().getContinueButtonId(), stepData.getUser())
		);
		buttons.forEach(button -> buttonManager.addButton(button.getId(), this::handleButtonPress));

		stepData.setStep(this);
		stepData.getMessage().editMessageEmbeds(embed).setActionRow(buttons).queue();
	}

	private void handleButtonPress(ButtonInteractionEvent event) {
		StepData stepData = stepService.getUser(event.getUser().getId());
		String buttonId = event.getComponentId();
		if (stepData.getStep() != this) return;

		final List<String> languages = languageService.getTranslations().keySet().stream().sorted().toList();

		if (buttonId.equalsIgnoreCase(stepConfig.getAdditionalLanguage().getContinueButtonId())) {
			stepData.setNextAllowed(true);
			List<String> additionalLanguages = stepData.getAdditionalLanguages();
			additionalLanguages.forEach(language -> personLanguageService.addAdditionalLanguage(stepData.getUser().getId(), language));

			stepService.nextStep(stepData);
			return;
		}

		if (languages.stream().anyMatch(language -> buttonId.startsWith(getName() + "-" + language))) {
			String langCode = buttonId.split("-")[1];
			Optional<String> language = languages.stream().filter(lang -> lang.equals(langCode)).findFirst();
			language.ifPresent(stepData.getAdditionalLanguages()::add);

			stepService.nextStep(stepData);
		}
	}

	@Override
	public String getName() {
		return "additionalLanguage";
	}
}
