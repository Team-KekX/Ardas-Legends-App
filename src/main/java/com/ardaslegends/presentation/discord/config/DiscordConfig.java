package com.ardaslegends.presentation.discord.config;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(BotProperties.class)
public class DiscordConfig {

    @Value("${ardaslegends.bot.token}")
    private String token;

    @Bean
    public DiscordApi api() {
        var api = new DiscordApiBuilder()
                .setToken(token)
                .setIntents(Intent.GUILDS, Intent.GUILD_MESSAGES, Intent.GUILD_MESSAGE_REACTIONS, Intent.GUILD_MEMBERS)
                .login().join();
        log.info("Logged in as {}", api.getClientId());
        return api;
    }
}
