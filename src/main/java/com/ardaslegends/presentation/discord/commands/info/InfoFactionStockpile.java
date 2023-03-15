package com.ardaslegends.presentation.discord.commands.info;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.FactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class InfoFactionStockpile implements ALCommandExecutor {

    private final FactionService factionService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /info faction stockpile request");

        log.debug("Getting options");
        String factionName = getStringOption("faction-name", options);
        log.debug("faction-name: [{}]", factionName);

        log.trace("Calling armyService");
        Faction faction = discordServiceExecution(factionName, factionService::getFactionByName, "Error while getting Faction Stockpile");

        log.debug("Building response Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Faction Stockpile")
                .setDescription("Current stockpile of Faction %s".formatted(faction.getName()))
                .setColor(ALColor.GREEN)
                .addInlineField("Faction", faction.getName())
                .addInlineField("Stockpile", "%d stacks".formatted(faction.getFoodStockpile()))
                .addField("Days of movement payable", "%d".formatted((int)Math.floor(faction.getFoodStockpile()/9.0)), false)
                .setThumbnail(getFactionBanner(faction.getName()))
                .setTimestampToNow());
    }
}
