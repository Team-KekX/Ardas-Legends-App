package com.ardaslegends.data.presentation.discord.commands.delete.staff;

import com.ardaslegends.data.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.DeleteArmyDto;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor

@Component
public class DeleteArmyCommand implements ALStaffCommandExecutor {

    private final ArmyService armyService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Handling /delete army request");

        checkStaff(interaction, properties.getStaffRoles());
        log.trace("DeleteArmy: User is staff -> authorized");

        String armyName = getStringOption("army", options);
        log.trace("DeleteArmy: armyName value is [{}]", armyName);

        log.trace("DeleteArmy: Building Dto");
        DeleteArmyDto dto = new DeleteArmyDto(interaction.getUser().getIdAsString(), armyName);

        log.debug("DeleteArmy: Calling Service Execution");
        var army = discordServiceExecution(dto, true, armyService::disband, "Error while deleting army");

        return new EmbedBuilder()
                .setTitle("Staff-Deleted Army / Company")
                .setDescription("Deleted army / company '%s' from the game!".formatted(army.getName()))
                .setThumbnail(getFactionBanner(army.getFaction().getName()))
                .setColor(ALColor.YELLOW)
                .setTimestampToNow();
    }
}
