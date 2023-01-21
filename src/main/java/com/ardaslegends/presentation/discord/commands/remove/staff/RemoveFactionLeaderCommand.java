package com.ardaslegends.presentation.discord.commands.remove.staff;

import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.FactionBanners;
import com.ardaslegends.service.FactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor
@Slf4j

public class RemoveFactionLeaderCommand implements ALStaffCommandExecutor {

    private final FactionService factionService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /remove faction leader request");

        checkStaff(interaction, properties.getStaffRoles());

        log.debug("Getting options");
        String factionName = getStringOption("faction-name", options);

        var result = discordServiceExecution(factionName, factionService::removeFactionLeader, "Error while removing faction leader");

        log.debug("Building response Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Removed Faction Leader")
                .setDescription("Removed %s from faction leader position in the faction %s".formatted(result.getIgn(), factionName))
                .setColor(ALColor.YELLOW)
                .setThumbnail(FactionBanners.getBannerUrl(factionName))
                .setTimestampToNow());
    }
}
