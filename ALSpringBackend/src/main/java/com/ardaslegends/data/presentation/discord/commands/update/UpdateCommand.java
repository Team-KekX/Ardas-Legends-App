package com.ardaslegends.data.presentation.discord.commands.update;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.update.staff.UpdatePlayerFactionCommand;
import com.ardaslegends.data.presentation.discord.commands.update.staff.UpdatePlayerIgnCommand;
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
public class UpdateCommand implements ALCommand {

    private final DiscordApi api;
    private final PlayerService playerService;

    @Override
    public void init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /update command");

        SlashCommand update = SlashCommand.with("update", "JAVACORD Updates information about an entity", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                        .setName("player")
                        .setDescription("Update Roleplay Character attributes")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("faction")
                                        .setDescription("Updates faction of a character")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("faction-name")
                                                        .setDescription("The name of the faction")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setName("player")
                                                        .setType(SlashCommandOptionType.USER)
                                                        .setDescription("The player that should change faction (Discord Ping)")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("ign")
                                        .setDescription("Update a player's Minecraft IGN")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.USER)
                                                        .setName("player")
                                                        .setDescription("Which player to change the ign of (Discord Ping)")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("ign")
                                                        .setDescription("The player's new ign")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()
                ))
                .createGlobal(api)
                .join();

        commands.put("update player faction", new UpdatePlayerFactionCommand(playerService)::execute);
        commands.put("update player ign", new UpdatePlayerIgnCommand(playerService)::execute);
        log.info("Finished initializing /update command");
    }
}
