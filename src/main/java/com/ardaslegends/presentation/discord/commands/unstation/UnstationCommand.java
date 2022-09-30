package com.ardaslegends.presentation.discord.commands.unstation;

import com.ardaslegends.presentation.discord.commands.ALCommand;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.service.ArmyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class UnstationCommand implements ALCommand {

    private final ArmyService armyService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /unstation command");
        var command = SlashCommand.with("unstation", "Unstation an army or company from a claimbuild", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("army-or-company")
                                .setDescription("Untation an army or company from a claimbuild")
                                .addOption(SlashCommandOption.createStringOption("army-or-company-name", "The name of the army/company", true))
                                .build()
                )
        );
        commands.put("unstation army-or-company", new UnstationArmyOrCompanyCommand(armyService));
        log.info("Finished initializing /unstation command");
        return command;
    }
}
