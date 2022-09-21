package com.ardaslegends.data.presentation.discord.commands.update;

import com.ardaslegends.data.domain.ClaimBuildType;
import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.create.CreateArmyCommand;
import com.ardaslegends.data.presentation.discord.commands.create.staff.CreateClaimbuildCommand;
import com.ardaslegends.data.presentation.discord.commands.create.staff.CreateRpCharCommand;
import com.ardaslegends.data.presentation.discord.commands.update.staff.UpdateRpcharFaction;
import com.ardaslegends.data.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
                        .setName("rpchar")
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
                                                        .setName("discord-id")
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setDescription("Discord ID of the target player")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build()
                ))
                .createGlobal(api)
                .join();

        commands.put("update rpchar faction", new UpdateRpcharFaction(playerService)::execute);
        log.info("Finished initializing /update command");
    }
}
