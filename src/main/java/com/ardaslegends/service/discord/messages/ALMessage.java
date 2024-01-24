package com.ardaslegends.service.discord.messages;

import jakarta.validation.constraints.NotNull;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public record ALMessage (MessageBuilder message, @NotNull EmbedBuilder embed) {

    public boolean hasMessage() {
        return message != null;
    }

}