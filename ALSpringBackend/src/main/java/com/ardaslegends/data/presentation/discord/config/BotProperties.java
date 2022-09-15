package com.ardaslegends.data.presentation.discord.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties("ardaslegends.bot")
public class BotProperties {

    public static String token;
    public static String server;
    public static String rpCommandsChannel;
    public static List<String> staffRoles;

    @Value("${ardaslegends.bot.token}")
    private void setToken(String token) {
        BotProperties.token = token;
    }
    @Value("${ardaslegends.bot.server}")
    private void setServer(String server) {
        BotProperties.server = server;
    }

    @Value("${ardaslegends.roleplay.commands.channel}")
    private void setRpCommandsChannel(String channel) {
        BotProperties.rpCommandsChannel = channel;
    }

    @Value("${ardaslegends.bot.staff-roles}")
    private void setStaffRoles(List<String> staffRoles) {
        BotProperties.staffRoles= staffRoles;
    }
}
