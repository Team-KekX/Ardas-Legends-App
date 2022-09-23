package com.ardaslegends.data.presentation.discord.commands.injure;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.move.MoveRpcharCommand;
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
public class InjureCommand implements ALCommand {

    private final PlayerService playerService;
    private final DiscordApi api;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /injure command");

        var command = SlashCommand.with("injure", "JAVACORD Injures your character", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("rpchar")
                                .setDescription("Injures your character")
                                .build()
                ));
        commands.put("injure rpchar", new InjureRpcharCommand(playerService));
        log.info("Finished initializing /injure command");
        return command;
    }
}
