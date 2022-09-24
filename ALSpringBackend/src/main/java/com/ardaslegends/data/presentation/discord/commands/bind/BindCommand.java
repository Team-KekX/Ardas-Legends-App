package com.ardaslegends.data.presentation.discord.commands.bind;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.service.ArmyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class BindCommand implements ALCommand {

    private final ArmyService armyService;

    public SlashCommandBuilder init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /bind command");
        var command = SlashCommand.with("bind", "Binds an army/company to a player", Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("army-or-company")
                                        .setDescription("Binds a character to an army or trading/armed company")
                                        .addOption(SlashCommandOption.createStringOption("army-or-company-name", "The name of the army/company", true))
                                        .addOption(SlashCommandOption.createUserOption("target-player", "Player that gets bound to the army, PING that discord account!", true))
                                        .build()
                        )
                );
        commands.put("bind army-or-company", new BindArmyOrCompanyCommand(armyService));
        log.info("Finished initializing /bind command");
        return command;
    }

}
