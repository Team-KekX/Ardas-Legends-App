package com.ardaslegends.presentation.discord.commands.declare;

import com.ardaslegends.presentation.discord.commands.ALCommand;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.service.war.WarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor

@Component
public class DeclareCommand implements ALCommand {

    private final DiscordApi api;

    private final WarService warService;


    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /declare commands");

        var command = SlashCommand.with("declare", "Declare different type of events like declare Wars and battles", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("war")
                        .setDescription("Declares war against another faction")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("war-name")
                                        .setDescription("The name that will be given to the war")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("attacked-faction-name")
                                        .setDescription("The name of the faction that you want to declare war to")
                                        .setRequired(true)
                                        .build()
                        ))
                        .build()
        ));

        commands.put("declare war", new DeclareWarCommand()::execute);
        return null;
    }
}
