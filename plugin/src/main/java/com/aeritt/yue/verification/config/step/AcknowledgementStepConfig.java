package com.aeritt.yue.verification.config.step;

import lombok.Getter;

@Getter
public class AcknowledgementStepConfig {
	private String acknowledgementEmbedId = "core.embed.acknowledgement.information";
	private String acceptationEmbedId = "verification.embed.verification.acceptation";
	private String readButtonId = "verification.button.verification.acceptation.read";
	private String acceptButtonId = "verification.button.verification.acceptation.accept";
}
