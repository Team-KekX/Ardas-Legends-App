package com.ardaslegends.data.presentation.discord.commands.cancel;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.move.MoveRpcharCommand;
import com.ardaslegends.data.service.MovementService;
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
public class CancelCommand implements ALCommand {

    private final MovementService movementService;
    private final DiscordApi api;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /cancel command");

        var command = SlashCommand.with("cancel", "JAVACORD Cancel an ongoing movement, healing, etc.", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                                .setName("move")
                                .setDescription("Cancels a map movement")
                                .setOptions(Arrays.asList(
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                                .setName("rpchar")
                                                .setDescription("Cancel an ongoing movement of a Roleplay Character")
                                                .build()
                                ))
                                .build()
                ));


        commands.put("cancel move rpchar", new CancelMoveRpcharCommand(movementService));
        log.info("Finished initializing /cancel command");
        return command;
    }
}
