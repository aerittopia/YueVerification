package com.aeritt.yue.verification;

import com.aeritt.yue.api.YuePlugin;
import com.aeritt.yue.verification.api.logic.StepService;
import com.aeritt.yue.verification.api.logic.StepStorage;
import com.aeritt.yue.verification.api.model.Step;
import com.aeritt.yue.verification.listener.ListenerRegistrar;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Verification extends YuePlugin implements com.aeritt.yue.verification.api.Verification {
	public Verification(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void onLoad() {
		//onLoad
	}

	@Override
	public void onEnable() {
		getApplicationContext().getBean(ListenerRegistrar.class).registerListeners();
		getApplicationContext().getBeansOfType(Step.class).values().
				forEach(step -> getApplicationContext().getBean(com.aeritt.yue.verification.logic.StepStorage.class).registerStep(step));
	}

	@Override
	public void onUnload() {

	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onReload() {

	}

	@Override
	public ApplicationContext createApplicationContext() {
		ApplicationContext parentContext = ((SpringPluginManager) getWrapper().getPluginManager()).getApplicationContext();

		AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
		childContext.setClassLoader(getWrapper().getPluginClassLoader());
		childContext.setParent(parentContext);
		childContext.registerBean(PluginWrapper.class, this::getWrapper);
		childContext.register(VerificationConfiguration.class);
		childContext.scan("com.aeritt.yue.verification");
		childContext.refresh();

		this.applicationContext = childContext;

		return childContext;
	}

	@Override
	public StepService getStepService() {
		return getApplicationContext().getBean(StepService.class);
	}

	@Override
	public StepStorage getStepStorage() {
		return getApplicationContext().getBean(StepStorage.class);
	}
}