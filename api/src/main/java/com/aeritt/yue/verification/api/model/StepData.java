package com.aeritt.yue.verification.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StepData {
	private Step step;
	private boolean nextAllowed;

	private User user;
	private Message message;
	private String mainLanguage;
	private List<String> additionalLanguages;
}
