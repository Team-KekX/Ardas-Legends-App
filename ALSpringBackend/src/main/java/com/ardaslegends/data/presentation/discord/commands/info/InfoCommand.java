package com.ardaslegends.data.presentation.discord.commands.info;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
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

        log.info("Finished initializing /info command");
        return command;
    }
}
