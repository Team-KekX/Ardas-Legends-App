package com.ardaslegends.data.presentation.discord.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter

@ConfigurationProperties("ardaslegends.bot")
public class BotProperties {

    private String token = "";
    private String server = "";
    private List<String> staffRoles = List.of();
}
