package com.ardaslegends.data.presentation.discord.commands.bind;

import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.service.ArmyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class Bind implements ALCommand {

    private final DiscordApi api;

    private final ArmyService armyService;

    public SlashCommand bind;

    public void init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /bind command");
        this.bind = SlashCommand.with("bind", "Binds an entity (army/company) to a player", Arrays.asList(
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.SUB_COMMAND)
                                        .setName("army-or-company")
                                        .setDescription("JAVACORD Binds a character to an army or trading/armed company")
                                        .addOption(SlashCommandOption.createStringOption("army-or-company-name", "The name of the army/company", true))
                                        .addOption(SlashCommandOption.createUserOption("target-player", "Player that gets bound to the army, PING that discord account!", true))
                                        .build()
                        )
                )
                .createGlobal(api)
                .join();
        commands.put("bind army-or-company", new BindArmyOrCompanyCommand()::execute);
        log.info("Finished initializing /bind command");
    }

}
