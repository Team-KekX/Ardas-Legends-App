package com.ardaslegends.data.presentation.discord.commands.station;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.unbind.UnbindArmyOrCompanyCommand;
import com.ardaslegends.data.service.ArmyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class StationCommand implements ALCommand {

    private final ArmyService armyService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /station command");
        var command = SlashCommand.with("station", "Station an army or company at a claimbuild", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("army-or-company")
                                .setDescription("Station an army or company at a claimbuild")
                                .addOption(SlashCommandOption.createStringOption("army-or-company-name", "The name of the army/company", true))
                                .addOption(
                                        new SlashCommandOptionBuilder()
                                                .setType(SlashCommandOptionType.STRING)
                                                .setName("claimbuild-name")
                                                .setDescription("The name of the claimbuild")
                                                .setRequired(true)
                                                .build()
                                )
                                .build()
                )
        );
        commands.put("station army-or-company", new StationArmyOrCompanyCommand(armyService));
        log.info("Finished initializing /station command");
        return command;
    }
}
