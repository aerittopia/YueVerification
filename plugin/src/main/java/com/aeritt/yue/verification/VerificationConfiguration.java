package com.aeritt.yue.verification;

import com.aeritt.yue.api.config.ConfigService;
import com.aeritt.yue.verification.config.SettingsConfig;
import com.aeritt.yue.verification.config.step.StepConfig;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class VerificationConfiguration {
	private final ApplicationContext ctx;

	@Autowired
	public VerificationConfiguration(ApplicationContext ctx) {
		this.ctx = ctx;

		registerConfigs();
	}

	public void registerConfigs() {
		ConfigService configService = ctx.getBean(ConfigService.class);

		configService.registerConfig(SettingsConfig.class, "", "settings.json");
		configService.registerConfig(StepConfig.class, "", "steps.json");
	}

	@Bean
	public Path dataPath() {
		PluginWrapper pluginWrapper = ctx.getBean(PluginWrapper.class);
		Path dataPath = pluginWrapper.getPluginPath().getParent().resolve(pluginWrapper.getPluginId() + "/");

		try {
			Files.createDirectories(dataPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataPath;
	}
}
