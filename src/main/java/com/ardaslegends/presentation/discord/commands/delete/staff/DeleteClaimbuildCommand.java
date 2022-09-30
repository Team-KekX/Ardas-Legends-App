package com.ardaslegends.presentation.discord.commands.delete.staff;

import com.ardaslegends.domain.Army;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.ClaimBuildService;
import com.ardaslegends.service.dto.claimbuilds.DeleteClaimbuildDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor

@Component
public class DeleteClaimbuildCommand implements ALStaffCommandExecutor {

    private final ClaimBuildService claimBuildService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /delete claimbuild request, getting option-data");

        checkStaff(interaction, properties.getStaffRoles());

        log.debug("Fetching option-data");
        String claimbuildName = getStringOption("claimbuild-name", options);

        DeleteClaimbuildDto dto = new DeleteClaimbuildDto(claimbuildName, null ,null);
        var claimbuild = discordServiceExecution(dto, claimBuildService::deleteClaimbuild, "Error during deletion of claimbuild");
        log.debug("DeleteClaimbuild: Result [{}]", claimbuild);

        String deletedArmies = claimbuild.getCreatedArmies().stream().map(Army::getName).collect(Collectors.joining(", "));
        String unstationedArmies = claimbuild.getStationedArmies().stream().map(Army::getName).collect(Collectors.joining(", "));

        return new EmbedBuilder()
                .setTitle("Staff-Deleted Claimbuild")
                .setColor(ALColor.YELLOW)
                .setDescription("Claimbuild %s of faction %s has been deleted".formatted(claimbuildName, claimbuild.getOwnedBy().getName()))
                .addField("Unstationed Armies/Companies", unstationedArmies.isEmpty() ? "None":unstationedArmies)
                .addField("Deleted Armies/Companies", deletedArmies.isEmpty() ? "None":deletedArmies)
                .setThumbnail(getFactionBanner(claimbuild.getOwnedBy().getName()))
                .setTimestampToNow();
    }
}
