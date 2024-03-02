package com.aeritt.yue.verification.api;

public class VerificationAPI {
	private static Verification instance;

	public VerificationAPI(Verification instance) {
		VerificationAPI.instance = instance;
	}

	public static Verification getInstance() {
		return instance;
	}
}
