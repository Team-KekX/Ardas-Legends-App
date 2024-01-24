package com.ardaslegends.presentation.discord.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Slf4j
@RequiredArgsConstructor
@Configuration
public class BotProperties {

    @Value("${ardaslgends.discord.client.id")
    private String clientId;

    @Value("${ardaslgends.discord.client.secret")
    private String clientSecret;

    @Value("${ardaslegends.bot.token}")
    private String token;
    @Value("${ardaslegends.bot.server}")
    private String serverId;
    private Server discordServer;
    @Value("${ardaslegends.roleplay.commands.channel}")
    private String rpCommandsChannel;
    private TextChannel rpAppsChannel;
    private TextChannel generalRpCommandsChannel;
    private TextChannel claimbuildAppsChannel;

    private TextChannel errorChannel;
    @Value("${ardaslegends.bot.staff-roles}")
    private List<String> staffRoleIds;

    private Set<Role> discordStaffRoles;

    private final DiscordApi api;


    public String getToken() {
        return token;
    }
    @Value("${ardaslegends.bot.server}")
    private void setDiscordServer(String serverId) {
        Objects.requireNonNull(serverId);
        log.trace("Fetching server with id [{}]", serverId);
        val server = api.getServerById(serverId).orElseThrow();
        log.info("Found Discord Server! Name {}, Id {}", server.getName(), server.getIdAsString());

        this.discordServer = server;
    }
    @Value("${ardaslegends.roleplay.commands.channel}")
    private void setRpCommandsChannel(String rpCommandsChannelId) {
        Objects.requireNonNull(rpCommandsChannelId);
        log.trace("Fetching rpCommandsChannel with id [{}]", rpCommandsChannelId);
        val channel = api.getTextChannelById(rpCommandsChannelId).orElseThrow();
        log.info("Found RpCommands Channel! getChannelByIdId {}", channel.getIdAsString());

        this.generalRpCommandsChannel = channel;
    }
    @Value("${ardaslegends.bot.staff-roles}")
    private void setDiscordStaffRoles(List<String> roleIds) {
        Objects.requireNonNull(roleIds);

        log.trace("Fetching roleId");
        val roles = roleIds.stream()
                .map(s -> api.getRoleById(s).orElse(null))
                .collect(Collectors.toSet());

        log.info("Found roles [{}]", roles.stream().map(Nameable::getName).collect(Collectors.joining(", ")));

        this.discordStaffRoles = roles;
    }

    @Value("${ardaslegends.roleplay.apps.channel}")
    private void setRpAppsChannel(String channelId) {
        Objects.requireNonNull(channelId);
        log.debug("Fetching rp-apps channel with id [{}]", channelId);

        val channel = api.getTextChannelById(channelId).orElseThrow();

        this.rpAppsChannel = channel;
        log.info("Found RpApps Channel [{}]", rpAppsChannel.getIdAsString());
    }

    @Value("${ardaslegends.claimbuild.apps.channel}")
    private void setClaimbuildAppsChannel(String channelId) {
        Objects.requireNonNull(channelId);
        log.trace("Fetching cb-apps channel with id [{}]", channelId);
        val channel = api.getTextChannelById(channelId).orElseThrow();
        log.info("Found CbApps Channel");

        this.claimbuildAppsChannel= channel;
    }

    @Value("${ardaslegends.bot.error.channel}")
    private void setErrorChannel(String errorChannelId) {
        Objects.requireNonNull(errorChannelId);
        log.debug("Fetching error channel with id [{}]", errorChannelId.substring(0, 2));

        val channel = api.getTextChannelById(errorChannelId).orElseThrow();

        this.errorChannel= channel;
        log.info("Found error Channel [{}]", errorChannel.getIdAsString());
    }

    public String getRpCommandsChannel() {
        return rpCommandsChannel;
    }

    public List<String> getStaffRoleIds() {
        return staffRoleIds;
    }
}
