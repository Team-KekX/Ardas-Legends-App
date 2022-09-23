package com.ardaslegends.data.presentation.discord.commands.move;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.update.staff.*;
import com.ardaslegends.data.service.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
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
    public void init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /move command");

        SlashCommand move = SlashCommand.with("move", "JAVACORD Starts a movement on the map", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("rpchar")
                        .setDescription("Starts a map movement of a roleplay character")
                        .setOptions(Arrays.asList(
                                SlashCommandOption.createStringOption("end-region", "The destination of the movement", true)
                        ))
                        .build()
                ))
                .createGlobal(api)
                .join();


        commands.put("move rpchar", new MoveRpcharCommand(movementService));
        log.info("Finished initializing /move command");
    }
}
