package com.ardaslegends.data.presentation.discord.commands.create;

import com.ardaslegends.data.domain.ClaimBuildType;
import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.create.staff.CreateClaimbuildCommand;
import com.ardaslegends.data.presentation.discord.commands.create.staff.CreateRpCharCommand;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionChoiceBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class CreateCommand implements ALCommand {

    private final DiscordApi api;
    private final PlayerService playerService;

    private final ClaimBuildService claimBuildService;
    @Override
    public void init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /create command");

        SlashCommand register = SlashCommand.with("create", "Creates an entity (RpChar, army, trader etc.)", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("rpchar")
                                .setDescription("JAVACOORD Creates a Roleplay Character")
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
                                .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("claimbuild")
                        .setDescription("Staff Command - Creates a claimbuild")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("name")
                                        .setDescription("Name of the Claimbuild")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("region")
                                        .setDescription("The id of the region the claimbuild is located in")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("type")
                                        .setDescription("The type of claimbuild")
                                        .setChoices(Arrays.stream(ClaimBuildType.values())
                                                .map(claimBuildType -> new SlashCommandOptionChoiceBuilder()
                                                        .setName(StringUtils.capitalize(claimBuildType.name()))
                                                        .setValue(claimBuildType.name())
                                                        .build())
                                                .toList()
                                        )
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("faction")
                                        .setDescription("The faction that owns this claimbuild")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.LONG)
                                        .setName("x")
                                        .setDescription("The x coordinate of the build")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.LONG)
                                        .setName("y")
                                        .setDescription("The y coordinate of the build")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.LONG)
                                        .setName("z")
                                        .setDescription("The z coordinate of the build")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("traders")
                                        .setDescription("The traders that the claimbuild has")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("sieges")
                                        .setDescription("Siege present at this build, Separate the sieges with ,")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("number-of-houses")
                                        .setDescription("Number of houses in the build, E.g. 14 small house, the bot does do anything with this input")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("built-by")
                                        .setDescription("Players who built the cb. Separate player with - Example: Luktronic-mirak441")
                                        .setRequired(true)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("production-sites")
                                        .setDescription("Production Sites in the cb. Example: Fishing Lodge:Salmon:2-Mine:Iron:5")
                                        .setRequired(false)
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("special-buildings")
                                        .setDescription("Separate the buildings with - Example: House of Healing-Embassy")
                                        .setRequired(false)
                                        .build()
                                ))
                        .build()    
                ))
                .createGlobal(api)
                .join();

        commands.put("create rpchar", new CreateRpCharCommand(playerService)::execute);
        commands.put("create claimbuild", new CreateClaimbuildCommand(claimBuildService)::execute);
        log.info("Finished initializing /create command");
    }
}
