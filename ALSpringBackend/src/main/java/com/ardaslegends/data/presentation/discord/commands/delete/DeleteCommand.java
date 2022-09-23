package com.ardaslegends.data.presentation.discord.commands.delete;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.delete.staff.DeleteClaimbuildCommand;
import com.ardaslegends.data.presentation.discord.commands.delete.staff.DeletePlayerCommand;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor

@Component
public class DeleteCommand implements ALCommand {

    private final DiscordApi api;
    private final ClaimBuildService claimBuildService;
    private final PlayerService playerService;

    @Override
    public void init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /delete commands");

        SlashCommand delete = SlashCommand.with("delete", "Deletes an Entity (RpChar, Player, Army, Claimbuild, etc)", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("claimbuild")
                                .setDescription("Javacord Deletes a claimbuild")
                                .setOptions(Arrays.asList(
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.STRING)
                                                .setName("claimbuild-name")
                                                .setDescription("The claimbuild that should be deleted")
                                                .setRequired(true)
                                                .build()
                                ))
                                .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("player")
                        .setDescription("Deletes a player entity, including their rpchar. Removes players from 'built by' in claimbuilds")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("target-player-id")
                                        .setDescription("The discordId of the player that is to be deleted")
                                        .setRequired(true)
                                        .build()
                        ))
                        .build()
                ))
                .createGlobal(api)
                .join();

        commands.put("delete claimbuild", new DeleteClaimbuildCommand(claimBuildService)::execute);
        commands.put("delete player", new DeletePlayerCommand(playerService)::execute);
    }
}
