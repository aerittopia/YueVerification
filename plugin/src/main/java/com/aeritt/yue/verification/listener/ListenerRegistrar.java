package com.aeritt.yue.verification.listener;

import com.aeritt.yue.verification.listener.guild.GuildMemberJoinListener;
import net.dv8tion.jda.api.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ListenerRegistrar {
	private final ApplicationContext ctx;
	private final JDA jda;

	@Autowired
	public ListenerRegistrar(@Qualifier ApplicationContext ctx, @Lazy JDA jda) {
		this.ctx = ctx;
		this.jda = jda;
	}

	public void registerListeners() {
		jda.addEventListener(ctx.getBean(GuildMemberJoinListener.class));
	}
}
