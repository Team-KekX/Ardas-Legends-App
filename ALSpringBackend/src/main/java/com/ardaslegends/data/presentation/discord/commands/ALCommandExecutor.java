package com.ardaslegends.data.presentation.discord.commands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;

public interface ALCommandExecutor {

    EmbedBuilder execute(SlashCommandInteraction interaction);
}
