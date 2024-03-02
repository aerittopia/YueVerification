package com.aeritt.yue.verification.api.model;

public abstract class Step {
	public abstract void execute(StepData stepData);

	public abstract String getName();
}