package com.ardaslegends.presentation.discord.commands.station;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.ArmyType;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.Thumbnails;
import com.ardaslegends.service.ArmyService;
import com.ardaslegends.service.dto.army.StationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class StationArmyOrCompanyCommand implements ALCommandExecutor {

    private final ArmyService armyService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /station army-or-company request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.debug("Getting options");
        String armyName = getStringOption("army-or-company-name", options);
        log.debug("armyName: [{}]", armyName);
        String cbName = getStringOption("claimbuild-name", options);
        log.debug("claimbuild-name: [{}]", cbName);

        log.trace("Building dto");
        StationDto dto = new StationDto(user.getIdAsString(), armyName, cbName);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling armyService");
        Army army = discordServiceExecution(dto, armyService::station, "Error while stationing Army/Company");

        String armyType = army.getArmyType().getName();

        String thumbnail = "";
        if (army.getArmyType() == ArmyType.ARMY) {
            thumbnail = getFactionBanner(army.getFaction().getName());
        } else {
            thumbnail = Thumbnails.BIND_TRADER.getUrl();
        }

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Stationed %s".formatted(armyType))
                .setDescription("The %s %s has been stationed in Claimbuild %s".formatted(armyType, army.getName(), army.getStationedAt().getName()))
                .setColor(ALColor.GREEN)
                .addInlineField("Army", army.getName())
                .addInlineField("Army Faction", army.getFaction().getName())
                .addInlineField("Bound Player", army.getBoundTo() == null ? "None" : army.getBoundTo().getIgn())
                .addInlineField("Claimbuild", army.getStationedAt().getName())
                .addInlineField("Region", army.getCurrentRegion().getId())
                .setThumbnail(thumbnail)
                .setTimestampToNow();
    }
}
