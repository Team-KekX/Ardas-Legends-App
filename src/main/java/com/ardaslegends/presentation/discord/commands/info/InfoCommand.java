package com.ardaslegends.presentation.discord.commands.info;

import com.ardaslegends.presentation.discord.commands.ALCommand;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.service.ArmyService;
import com.ardaslegends.service.FactionService;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class InfoCommand implements ALCommand {

    private final PlayerService playerService;
    private final ArmyService armyService;
    private final FactionService factionService;
    private final RegionService regionService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /info command");

        var command = SlashCommand.with("info", "General Information about Armies, Factions, Regions, etc..", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND_GROUP)
                        .setName("faction")
                        .setDescription("Information about factions")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("upkeep")
                                        .setDescription("Shows the amount of coins a faction needs to pay")
                                        .setOptions(Arrays.asList(
                                                new SlashCommandOptionBuilder()
                                                        .setType(SlashCommandOptionType.STRING)
                                                        .setName("faction-name")
                                                        .setDescription("The name of the faction which's upkeep should be displayed")
                                                        .setRequired(true)
                                                        .build()
                                        ))
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("stockpile")
                                        .setDescription("Displays the faction's current food stockpile")
                                        .addOption(SlashCommandOption.createStringOption("faction-name", "Name of the faction", true))
                                        .build()
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("unpaid-armies-or-companies")
                        .setDescription("Displays the 10 oldest armies and companies which have NOT been payed for")
                        .build()
        ));

        commands.put("info faction upkeep", new InfoFactionUpkeepCommand(armyService));
        commands.put("info unpaid-armies-or-companies", new InfoUnpaidArmiesOrCompanies(armyService));
        commands.put("info faction stockpile", new InfoFactionStockpile(factionService));

        log.info("Finished initializing /info command");
        return command;
    }
}
