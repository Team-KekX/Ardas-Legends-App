package com.ardaslegends.presentation.discord.commands.move;

import com.ardaslegends.presentation.discord.commands.ALCommand;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.service.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class MoveCommand implements ALCommand {

    private final MovementService movementService;
    private final DiscordApi api;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /move command");

        var command = SlashCommand.with("move", "JAVACORD Starts a movement on the map", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("rpchar")
                        .setDescription("Starts a map movement of a roleplay character")
                        .setOptions(Arrays.asList(
                                SlashCommandOption.createStringOption("destination-region", "The destination region of the movement", true)
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("army-or-company")
                        .setDescription("Starts a map movement of an army/company")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("army-or-company-name")
                                        .setDescription("The army's/company's name")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("destination-region")
                                        .setDescription("The destination region of the movement")
                                        .setRequired(true)
                                        .build()
                        ))
                        .build()
                ));


        commands.put("move rpchar", new MoveRpcharCommand(movementService));
        commands.put("move army-or-company", new MoveArmyOrCompanyCommand(movementService));
        log.info("Finished initializing /move command");
        return command;
    }
}
