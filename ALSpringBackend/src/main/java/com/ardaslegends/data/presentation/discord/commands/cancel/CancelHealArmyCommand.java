package com.ardaslegends.data.presentation.discord.commands.cancel;

import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.Thumbnails;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.UpdateArmyDto;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor
public class CancelHealArmyCommand implements ALCommandExecutor {

    private final ArmyService armyService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("CancelHealArmy: Incoming /cancel heal army request");

        String discordId = interaction.getUser().getIdAsString();

        String armyName = getStringOption("army", options);
        log.trace("CancelHealArmy: armyName is [{}]", armyName);

        log.trace("CancelHealArmy: Building UpdateArmyDto");
        UpdateArmyDto dto = new UpdateArmyDto(discordId, armyName, null, null);

        log.debug("CancelHealArmy: Calling armyService cancelArmy");
        var army = discordServiceExecution(dto, armyService::healStop, "Error while stopping heal");

        log.debug("CancelHealArmy: Building EmbedBuilder");
        return new EmbedBuilder()
                .setTitle("Cancelled healing of army")
                .setDescription("The healing of army '%s' has been stopped \nSome units may not be healed!".formatted(army.getName()))
                .addField("Units", createUnitsAliveString(army.getUnits()))
                .setThumbnail(getFactionBanner(army.getFaction().getName()))
                .setTimestampToNow()
                .setColor(ALColor.GREEN);
    }
}
