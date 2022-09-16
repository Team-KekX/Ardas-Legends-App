package com.ardaslegends.data.presentation.discord.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties("ardaslegends.bot")
public class BotProperties {

    @Value("${ardaslegends.bot.token}")
    public String token;
    @Value("${ardaslegends.bot.server}")
    static String server;
    @Value("${ardaslegends.roleplay.commands.channel}")
    private String rpCommandsChannel;
    @Value("${ardaslegends.bot.staff-roles}")
    private List<String> staffRoles;

    public String getToken() {
        return token;
    }

    public static String getServer() {
        return server;
    }

    public String getRpCommandsChannel() {
        return rpCommandsChannel;
    }

    public List<String> getStaffRoles() {
        return staffRoles;
    }
}
