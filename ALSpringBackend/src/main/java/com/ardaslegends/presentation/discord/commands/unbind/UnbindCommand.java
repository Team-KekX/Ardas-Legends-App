package com.ardaslegends.presentation.discord.commands.unbind;

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
public class UnbindCommand implements ALCommand {

    private final ArmyService armyService;

    @Override
    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /unbind command");
        var command = SlashCommand.with("unbind", "Unbinds a player from an army/company", Arrays.asList(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.SUB_COMMAND)
                                .setName("army-or-company")
                                .setDescription("Unbinds a character from an army or trading/armed company")
                                .addOption(SlashCommandOption.createStringOption("army-or-company-name", "The name of the army/company", true))
                                .addOption(SlashCommandOption.createUserOption("target-player", "Player that gets unbound from the army, PING that discord account!", true))
                                .build()
                )
        );
        commands.put("unbind army-or-company", new UnbindArmyOrCompanyCommand(armyService));
        log.info("Finished initializing /unbind command");
        return command;
    }
}
