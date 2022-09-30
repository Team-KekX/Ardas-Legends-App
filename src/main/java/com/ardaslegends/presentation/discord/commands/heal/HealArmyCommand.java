package com.ardaslegends.presentation.discord.commands.heal;

import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.Thumbnails;
import com.ardaslegends.service.ArmyService;
import com.ardaslegends.service.dto.army.UpdateArmyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class HealArmyCommand implements ALCommandExecutor {

    private final ArmyService armyService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /heal army request");


        String discordId = interaction.getUser().getIdAsString();

        String armyName = getStringOption("army", options);
        log.trace("HealArmy: ArmyName is [{}]", armyName);


        log.trace("HealArmy: Building Dto");
        UpdateArmyDto dto = new UpdateArmyDto(discordId, armyName, null, null);

        log.debug("HealArmy: Calling ArmyService execution");
        var army = discordServiceExecution(dto, armyService::healStart, "Error while starting heal of army");

        log.trace("HealArmy: Building EmbedBuilder");
        return new EmbedBuilder()
                .setTitle("Healing of army started!")
                .setColor(ALColor.GREEN)
                .setThumbnail(Thumbnails.HEAL.getUrl())
                .setTimestampToNow()
                .setDescription("Army " + army.getName() + "has started healing in the claimbuild of " + army.getStationedAt().getName())
                .addInlineField("Army", army.getName())
                .addInlineField("Faction of Army", army.getFaction().getName())
                .addField("Region", army.getStationedAt().getRegion().getId())
                .addInlineField("Claimbuild", army.getStationedAt().getName())
                .addInlineField("Faction of Claimbuild", army.getStationedAt().getOwnedBy().getName());

    }
}
