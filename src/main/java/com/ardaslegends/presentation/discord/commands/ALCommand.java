package com.ardaslegends.presentation.discord.commands;

import org.javacord.api.interaction.SlashCommandBuilder;

import java.util.Map;

public interface ALCommand {

    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands);
}
