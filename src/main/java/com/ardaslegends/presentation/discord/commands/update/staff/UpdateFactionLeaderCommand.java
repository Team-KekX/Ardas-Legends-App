package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.FactionService;
import com.ardaslegends.service.dto.UpdateFactionLeaderDto;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor
public class UpdateFactionLeaderCommand implements ALStaffCommandExecutor {

    private final FactionService factionService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /update faction leader request");

        checkStaff(interaction, properties.getStaffRoles());
        log.trace("UpdateFactionLeader: User is staff -> allowed");

        var user = getUserOption("new-leader", options);
        log.trace("UpdateFactionLeader: User is [{}, {}]", user.getDiscriminatedName(), user.getIdAsString());

        var factionName = getStringOption("faction-name", options);
        log.trace("UpdateFactionLeader: FactionName is [{}]", factionName);

        UpdateFactionLeaderDto dto = new UpdateFactionLeaderDto(factionName, user.getIdAsString());
        var result = discordServiceExecution(dto, factionService::setFactionLeader, "Error while changing faction leader");
        log.trace("UpdateFactionLeader: Faction [{}] with leader [{}]", result.getName());

        log.debug("UpdateFactionLeader: Building Embed");
        return new EmbedBuilder()
                .setTitle("Changed Faction Leader")
                .setDescription("Staff changed the faction leader of %s".formatted(result.getName()))
                .setColor(ALColor.YELLOW)
                .setThumbnail(getFactionBanner(result.getName()))
                .addInlineField("Faction", result.getName())
                .addInlineField("New Leader", result.getLeader().getIgn() + " / " + user.getMentionTag());
    }
}
