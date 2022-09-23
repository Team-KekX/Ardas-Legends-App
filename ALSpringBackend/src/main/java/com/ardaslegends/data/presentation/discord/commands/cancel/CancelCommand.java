package com.ardaslegends.data.presentation.discord.commands.cancel;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.move.MoveRpcharCommand;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.PlayerService;
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
    private final PlayerService playerService;
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
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                        .setName("heal")
                        .setDescription("Cancels an ongoing healing")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("rpchar")
                                        .setDescription("Cancels the ongoing healing of your Roleplay Character")
                                        .build()
                        ))
                        .build()
                ));


        commands.put("cancel move rpchar", new CancelMoveRpcharCommand(movementService));
        commands.put("cancel heal rpchar", new CancelHealRpcharCommand(playerService));
        log.info("Finished initializing /cancel command");
        return command;
    }
}
