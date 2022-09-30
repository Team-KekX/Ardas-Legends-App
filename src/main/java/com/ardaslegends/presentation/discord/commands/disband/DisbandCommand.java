package com.ardaslegends.presentation.discord.commands.disband;

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
public class DisbandCommand implements ALCommand {

    private final ArmyService armyService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /disband command");
        var command = SlashCommand.with("disband", "Disbands an army/company", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("army-or-company")
                                .setDescription("Disbands an army/company")
                                .addOption(SlashCommandOption.createStringOption("army-or-company-name", "The name of the army/company", true))
                                .build()
                )
        );
        commands.put("disband army-or-company", new DisbandArmyOrcompanyCommand(armyService));
        log.info("Finished initializing /disband command");
        return command;
    }
}
