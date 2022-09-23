package com.ardaslegends.data.presentation.discord.commands;

import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface ALCommandExecutor extends DiscordUtils {

    EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties);
}
