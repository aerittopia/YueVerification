package com.aeritt.yue.template;

import me.whereareiam.yue.api.YuePlugin;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TemplatePlugin extends YuePlugin {
	public TemplatePlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {

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
		childContext.register(TemplateConfiguration.class);
		childContext.scan("com.aeritt.yue.template");
		childContext.refresh();

		this.applicationContext = childContext;

		return childContext;
	}
}