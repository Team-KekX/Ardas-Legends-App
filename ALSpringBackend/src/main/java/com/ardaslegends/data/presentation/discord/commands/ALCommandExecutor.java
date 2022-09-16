package com.ardaslegends.data.presentation.discord.commands;

import com.ardaslegends.data.presentation.discord.config.BotProperties;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.springframework.beans.factory.annotation.Autowired;

public interface ALCommandExecutor {

    EmbedBuilder execute(SlashCommandInteraction interaction, BotProperties properties);
}
