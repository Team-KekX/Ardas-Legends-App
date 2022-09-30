package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.ClaimBuildService;
import com.ardaslegends.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Component
public class UpdateClaimbuildFactionCommand implements ALStaffCommandExecutor {

    private final ClaimBuildService claimBuildService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Handling /update claimbuild faction");

        checkStaff(interaction, properties.getStaffRoles());
        log.trace("UpdateClaimbuildFaction: Player is staff -> Allowed to execute");

        String claimbuildName = getStringOption("claimbuild",options);
        log.trace("UpdateClaimbuildFaction: Claimbuild name is [{}]", claimbuildName);

        String newFactionName = getStringOption("faction", options);
        log.trace("UpdateClaimbuildFaction: New Faction name is [{}]", newFactionName);

        log.trace("UpdateClaimbuildFaction: Building Dto");
        UpdateClaimbuildOwnerDto dto = new UpdateClaimbuildOwnerDto(claimbuildName, newFactionName);

        log.debug("UpdateClaimbuildFaction: Calling ServiceExecution");
        var claimbuild = discordServiceExecution(dto, claimBuildService::setOwnerFaction, "Error while updating owning Faction of Claimbuild");


        return new EmbedBuilder()
                .setTitle("Updated Claimbuild controlling Faction!")
                .setColor(ALColor.YELLOW)
                .setDescription("New controlling Faction of Claimbuild '%s' is Faction '%s'".formatted(claimbuild.getName(), claimbuild.getOwnedBy().getName()))
                .setThumbnail(getFactionBanner(claimbuild.getOwnedBy().getName()))
                .setTimestampToNow();
    }
}
