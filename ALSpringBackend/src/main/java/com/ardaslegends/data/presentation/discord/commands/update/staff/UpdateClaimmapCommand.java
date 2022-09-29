package com.ardaslegends.data.presentation.discord.commands.update.staff;

import com.ardaslegends.data.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UpdateClaimmapCommand implements ALStaffCommandExecutor {

    private final RegionService regionService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /update claimmap");

        checkStaff(interaction, properties.getStaffRoles());
        log.trace("UpdateClaimmap: User is staff");

        var result = discordServiceExecution(regionService::resetHasOwnership, "Error while resetting claimmap");
        log.trace("UpdateClaimmap: Result [{}]", result);

        String regionString = result.stream()
                .map(region -> region.getId())
                .collect(Collectors.joining(", "));

        log.debug("UpdateClaimmap: Building Embed");
        return new EmbedBuilder()
                .setTitle("Reset regions in claimmap update")
                .setDescription("Regions that have changed ownership since last update: \n" + regionString)
                .setColor(ALColor.YELLOW)
                .setTimestampToNow();
    }
}
