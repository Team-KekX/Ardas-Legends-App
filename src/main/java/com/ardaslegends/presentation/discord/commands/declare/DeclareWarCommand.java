package com.ardaslegends.presentation.discord.commands.declare;


import com.ardaslegends.domain.war.War;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.service.dto.war.CreateWarDto;
import com.ardaslegends.service.war.WarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Component
public class DeclareWarCommand implements ALCommandExecutor {

    private final WarService warService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /declare war request");

        String executorDiscordId = interaction.getUser().getIdAsString();

        String warName = getStringOption("war-name", options);
        log.trace("war-name is -> {}", warName);

        String attackedFactionName = getStringOption("attacked-faction-name", options);
        log.trace("attacked-faction-name is -> {}", attackedFactionName);

        CreateWarDto warDto = new CreateWarDto(executorDiscordId, warName, attackedFactionName);

        War war = discordServiceExecution(warDto, warService::createWar, "Error while declaring war");

        return null;
    }
}
