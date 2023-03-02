package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.domain.ArmyType;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class UpdateArmyFreeTokensCommand implements ALStaffCommandExecutor {

    private final ArmyService armyService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Handling /update army free-tokens command, fetching option-data");

        checkStaff(interaction, properties.getStaffRoleIds());

        String armyName = getStringOption("army-name", options);
        log.trace("UpdateArmyPaid: army-name is [{}]", armyName);

        Double freeTokens = getDecimalOption("free-tokens", options);
        log.trace("UpdateArmyPaid: free-tokens attribute is [{}]", freeTokens);

        log.trace("UpdateArmyPaid: Building Dto");
        UpdateArmyDto dto = new UpdateArmyDto(null, armyName, freeTokens, null);

        log.debug("UpdateArmyPaid: Calling Service Execution");
        var army = discordServiceExecution(dto, armyService::setFreeArmyTokens, "Error while updating free Army/Company tokens");

        String armyType = army.getArmyType().getName();

        String thumbnail = "";
        if (army.getArmyType() == ArmyType.ARMY) {
            thumbnail = getFactionBanner(army.getFaction().getName());
        } else {
            thumbnail = Thumbnails.BIND_TRADER.getUrl();
        }

        DecimalFormat df = new DecimalFormat("0.##");
        df.setRoundingMode(RoundingMode.DOWN);

        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Updated %s's Tokens".formatted(armyType))
                .setDescription("Updated the free tokens of %s %s".formatted(armyType, army.getName()))
                .addInlineField("%s".formatted(armyType), army.getName())
                .addInlineField("Faction", army.getFaction().getName())
                .addInlineField("Tokens", "%s/30".formatted(df.format(army.getFreeTokens())))
                .addField("Units", createArmyUnitListString(army), false)
                .setColor(ALColor.YELLOW)
                .setThumbnail(thumbnail)
                .setTimestampToNow());
    }
}
