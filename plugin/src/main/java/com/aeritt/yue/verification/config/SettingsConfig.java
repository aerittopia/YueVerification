package com.aeritt.yue.verification.config;

import lombok.Getter;

import java.util.List;

@Getter
public class SettingsConfig {
	private String verifiedRoleId = "856952987896774696";
	private List<String> stepOrder = List.of(
			"mainLanguage",
			"additionalLanguage",
			"acknowledgement",
			"welcome"
	);
}
