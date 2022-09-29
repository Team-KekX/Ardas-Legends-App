package com.ardaslegends.data.presentation.discord.commands.update;

import com.ardaslegends.data.domain.ClaimBuildType;
import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.update.staff.*;
import com.ardaslegends.data.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class UpdateCommand implements ALCommand {

    private final DiscordApi api;
    private final PlayerService playerService;
    private final ClaimBuildService claimBuildService;
    private final ArmyService armyService;
    private final RegionService regionService;
    private final FactionService factionService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /update command");

        var command = SlashCommand.with("update", "JAVACORD Updates information about an entity", Arrays.asList(
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
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("discord-id")
                                        .setDescription("Update a player's Discord-ID")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("old-discord-id")
                                                        .setDescription("The player's old Discord ID")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("new-discord-id")
                                                        .setDescription("The player's new Discord ID")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                        .setName("rpchar")
                        .setDescription("Updates Roleplay Character Values")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("gear")
                                        .setDescription("Updates the gear of a Roleplay Character")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.USER)
                                                        .setName("player")
                                                        .setDescription("The player whose RpChar to update (Discord Ping)")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("new-gear")
                                                        .setDescription("The new gear of the RpChar")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("name")
                                        .setDescription("Updates name of an RpChar")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.USER)
                                                        .setName("player")
                                                        .setDescription("The player whose RpChar to update (Discord Ping)")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("new-name")
                                                        .setDescription("The new name of the RpChar")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("pvp")
                                        .setDescription("Updates the PvP attribute of a Roleplay Character")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.USER)
                                                        .setName("player")
                                                        .setDescription("The player whose RpChar to update (Discord Ping)")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.BOOLEAN)
                                                        .setName("new-pvp")
                                                        .setDescription("New PvP Value")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("title")
                                        .setDescription("Updates the Title of a Roleplay Character")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.USER)
                                                        .setName("player")
                                                        .setDescription("The player whose RpChar to update (Discord Ping)")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("new-title")
                                                        .setDescription("The character's new title")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                        .setName("claimbuild")
                        .setDescription("Updates Claimbuild Values")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("values")
                                        .setDescription("Completely redefines an existing claimbuild")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("cbname")
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
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("faction")
                                        .setDescription("Updates the controlling faction of a claimbuild")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("claimbuild")
                                                        .setDescription("The claimbuild who's controlling faction is to be updated")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("Faction")
                                                        .setDescription("The faction that now controlls the claimbuild")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                        .setName("army")
                        .setDescription("Updates Army Values")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("paid")
                                        .setDescription("Updates the value that represents if an army creation has been paid for!")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("army")
                                                        .setDescription("The name of the army that is to be updated")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.BOOLEAN)
                                                        .setName("paid")
                                                        .setDescription("True or False Value for the army")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                        .setName("faction")
                        .setDescription("Updates faction values")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("leader")
                                        .setDescription("Changes the leader a faction to the provided player")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.USER)
                                                        .setName("new-leader")
                                                        .setDescription("The player who should become the factions next leader!")
                                                        .setRequired(true)
                                                        .build(),
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("faction-name")
                                                        .setDescription("Thhe faction whose leader should be changed")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("claimmap")
                        .setDescription("Staff Commmand - Resets the hasOwnershipChanged to false for all regions")
                        .build()
        ));

        commands.put("update player faction", new UpdatePlayerFactionCommand(playerService));
        commands.put("update player ign", new UpdatePlayerIgnCommand(playerService));
        commands.put("update player discord-id", new UpdatePlayerDiscordIdCommand(playerService));

        commands.put("update rpchar gear", new UpdateRpcharGearCommand(playerService));
        commands.put("update rpchar pvp", new UpdateRpcharPvpCommand(playerService));
        commands.put("update rpchar name", new UpdateRpcharNameCommand(playerService));
        commands.put("update rpchar title", new UpdateRpcharTitleCommand(playerService));

        commands.put("update claimbuild values", new UpdateClaimbuildValues(claimBuildService));
        commands.put("update claimbuild faction", new UpdateClaimbuildFactionCommand(claimBuildService));

        commands.put("update army paid", new UpdateArmyPaidCommand(armyService));

        commands.put("update faction leader", new UpdateFactionLeaderCommand(factionService));

        commands.put("update claimmap", new UpdateClaimmapCommand(regionService));
        log.info("Finished initializing /update command");
        return command;
    }
}
