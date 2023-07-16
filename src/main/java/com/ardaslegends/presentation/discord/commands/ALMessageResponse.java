package com.ardaslegends.presentation.discord.commands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import jakarta.validation.constraints.NotNull;

public record ALMessageResponse(MessageBuilder message, @NotNull EmbedBuilder embed) {

    public boolean hasMessage() {
        return message != null;
    }

}
