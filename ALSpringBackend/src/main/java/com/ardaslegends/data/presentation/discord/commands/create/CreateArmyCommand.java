package com.ardaslegends.data.presentation.discord.commands.create;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.ArmyType;
import com.ardaslegends.data.domain.UnitType;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.dto.unit.UnitTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

@RequiredArgsConstructor

@Slf4j
public class CreateArmyCommand implements ALCommandExecutor, DiscordUtils {

    private final ArmyService armyService;


    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction) {
        log.debug("Incoming /create army request");

        User user = interaction.getUser();

        log.trace("Getting input data");
        String armyName = getStringOption("army-name", interaction);
        log.debug("armyName: [{}]", armyName);
        String cbName = getStringOption("claimbuild-name", interaction);
        log.debug("cbName: [{}]", cbName);
        String units = getStringOption("units", interaction);
        log.debug("units: [{}]", units);

        log.trace("Building unitTypeDtos");
        UnitTypeDto[] unitTypes = armyService.convertUnitInputIntoUnits(units);
        log.debug("Built UnitTypeDto[]: [{}]", (Object[]) unitTypes);

        log.trace("Building CreateArmyDto");
        CreateArmyDto dto = new CreateArmyDto(user.getIdAsString(), armyName, ArmyType.ARMY, cbName, unitTypes);
        log.debug("Built CreateArmyDto [{}]", dto);

        log.trace("Calling armyService.createArmy");
        Army army = discordServiceExecution(dto, armyService::createArmy, "Error while creating Army");

        String unitString = createArmyUnitListString(army);
        String paymentString = army.getIsPaid() ? "Army is free, no payment needed"
                : "Army is not free\nPlace 1000 Coins in a Pouch with the Army Name in the payment area!";

        log.debug("Building Response Embed");
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Army '%s' created".formatted(army.getName()))
                .setDescription("JAVACORD A new army '%s' has been raised by %s!".formatted(army.getName(), army.getFaction().getName()))
                .setColor(ALColor.GREEN)
                .addInlineField("Name", army.getName())
                .addInlineField("Faction", army.getFaction().getName())
                .addInlineField("Free Tokens", army.getFreeTokens().toString())
                .addInlineField("Region", army.getCurrentRegion().getId())
                .addInlineField("Units", unitString)
                .addField("Payment", paymentString, false);

        log.debug("Successfully built response embed - exiting execute function");
        return embed;
    }
}
