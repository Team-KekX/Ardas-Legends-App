package com.ardaslegends.data.presentation.discord.commands.create;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.create.staff.CreateRpChar;
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

@RequiredArgsConstructor

@Slf4j
@Component
public class Create implements ALCommand {

    private final DiscordApi api;
    private final PlayerService playerService;

    @Override
    public void init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /create command");

        SlashCommand register = SlashCommand.with("create", "Creates an entity (RpChar, army, trader etc.)", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("rpchar")
                                .setDescription("Creates a Roleplay Character")
                                .setOptions(Arrays.asList(
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.USER)
                                                .setName("target-player")
                                                .setDescription("The player who the char should be created for")
                                                .setRequired(true)
                                                .build(),
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.STRING)
                                                .setName("name")
                                                .setDescription("Character's name")
                                                .setRequired(true)
                                                .build(),
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.STRING)
                                                .setName("title")
                                                .setDescription("Character's title")
                                                .setRequired(true)
                                                .build(),
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.STRING)
                                                .setName("gear")
                                                .setDescription("Character's gear")
                                                .setRequired(true)
                                                .build(),
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.BOOLEAN)
                                                .setName("pvp")
                                                .setDescription("Should the character participate in PvP?")
                                                .setRequired(true)
                                                .build()
                                ))
                                .build()
                ))
                .createGlobal(api)
                .join();

        commands.put("create rpchar", new CreateRpChar(playerService)::execute);
        log.info("Finished initializing /create command");
    }
}
