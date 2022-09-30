package com.ardaslegends.data.presentation.discord.commands.stockpile;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.stockpile.staff.StockpileAddCommand;
import com.ardaslegends.data.presentation.discord.commands.stockpile.staff.StockpileRemoveCommand;
import com.ardaslegends.data.service.FactionService;
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
public class StockpileCommand implements ALCommand {

    private final FactionService factionService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /stockpile command");

        var command = SlashCommand.with("stockpile", "Stockpile functionality", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("add")
                        .setDescription("Adds an amount to the factions stockpile")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("faction")
                                        .setRequired(true)
                                        .setDescription("The faction whose stockpile gets changed")
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.LONG)
                                        .setName("amount")
                                        .setDescription("The amount that gets added to the stockpile")
                                        .setRequired(true)
                                        .build()
                        ))
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.SUB_COMMAND)
                        .setName("remove")
                        .setDescription("Removes an amount to the factions stockpile")
                        .setOptions(Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("faction")
                                        .setRequired(true)
                                        .setDescription("The faction whose stockpile gets changed")
                                        .build(),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.LONG)
                                        .setName("amount")
                                        .setDescription("The amount that gets removed from the stockpile")
                                        .setRequired(true)
                                        .build()
                        ))
                        .build()
        ));

        commands.put("stockpile add", new StockpileAddCommand(factionService));
        commands.put("stockpile remove", new StockpileRemoveCommand(factionService));

        log.info("Finished initializing /stockpile commands");
        return command;
    }
}
