package com.ardaslegends.service.discord.messages;

import jakarta.validation.constraints.NotNull;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.List;

public record ALMessage (MessageBuilder message, @NotNull List<EmbedBuilder> embeds) {

    public ALMessage {
        if(message != null)
            message.addEmbeds(embeds);
    }

    public boolean hasMessage() {
        return message != null;
    }

    @Override
    public String toString() {
        return "ALMessage{" +
                "message=" + message +
                ", embeds=" + embeds +
                '}';
    }
}