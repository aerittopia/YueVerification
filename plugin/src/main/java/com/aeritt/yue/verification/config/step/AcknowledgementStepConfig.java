package com.aeritt.yue.verification.config.step;

import lombok.Getter;

@Getter
public class AcknowledgementStepConfig {
	private String acknowledgementEmbedId = "core.embed.default.acknowledgement";
	private String acceptationEmbedId = "verification.embed.verification.acceptation";
	private String readButtonId = "core.button.default.read";
	private String acceptButtonId = "core.button.default.accept";
}
