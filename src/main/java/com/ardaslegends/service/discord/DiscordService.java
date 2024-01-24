package com.ardaslegends.service.discord;

import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.service.discord.messages.ALMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class DiscordService {


    private final BotProperties botProperties;
    private final DiscordApi discordApi;

    public Optional<Role> getRoleById(Long roleId) {
        log.debug("Getting discord role with id [{}]", roleId);
        val foundRole = discordApi.getRoleById(roleId);

        if(foundRole.isPresent())
            log.debug("Found role [{}]", foundRole.get().getName());
        else
            log.debug("No role with id [{}] found", roleId);
        return foundRole;
    }

    public Message sendMessageToRpChannel(ALMessage message) {
        log.debug("Trying to send message [{}] to Roleplay Channel", message);
        return sendMessage(message, botProperties.getGeneralRpCommandsChannel());
    }

    private Message sendMessage(ALMessage message, TextChannel channel) {
        log.debug("Trying to send message [{}] to channel [{}]", message, channel.getIdAsString());

        Objects.requireNonNull(message, "Message was null!");
        Objects.requireNonNull(channel, "TextChannel was null!");

        Message returnMessage = null;
        if(message.hasMessage()) {
            log.debug("Message has a message [{}]", message.message());
            returnMessage = message.message().send(channel).join();
        }
        else {
            log.debug("Message only consists of {} embeds [{}]", message.embeds().size(), message.embeds());
            returnMessage = channel.sendMessage(message.embeds()).join();
        }

        return returnMessage;
    }
}
