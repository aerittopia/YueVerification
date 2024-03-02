package com.aeritt.yue.verification.listener.guild;

import com.aeritt.yue.verification.api.logic.StepService;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuildMemberJoinListener extends ListenerAdapter {
	private final StepService stepService;

	@Autowired
	public GuildMemberJoinListener(StepService stepService) {
		this.stepService = stepService;
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		stepService.verifyUser(event.getUser());
	}
}