package com.ardaslegends.presentation.discord.commands.move;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.Movement;
import com.ardaslegends.domain.Player;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.MovementService;
import com.ardaslegends.service.dto.army.MoveArmyDto;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class MoveArmyOrCompanyCommand implements ALCommandExecutor {

    private final MovementService movementService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /move army-or-company request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.debug("Getting options");
        String armyName = getStringOption("army-or-company-name", options);
        log.debug("army/company Name: [{}]", armyName);
        String endRegion = getStringOption("destination-region", options);
        log.debug("destination-region: [{}]", endRegion);

        log.trace("Building dto");
        MoveArmyDto dto = new MoveArmyDto(user.getIdAsString(), armyName, endRegion);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling movementService");
        Movement movement = discordServiceExecution(dto, movementService::createArmyMovement, "Error while starting Army/Company Movement");

        Army army = movement.getArmy();
        Player boundPlayer = army.getBoundTo();

        Integer foodCost = ServiceUtils.getFoodCost(movement.getPath());

        log.debug("Building response Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Started %s Movement".formatted(army.getArmyType().getName()))
                .setDescription("The %s %s has started its movement towards region %s.\n%d Stacks of food were removed from food stockpile of faction %s"
                        .formatted(army.getArmyType().getName(), army.getName(), movement.getDestinationRegionId(), foodCost, army.getFaction().getName()))
                .setColor(ALColor.GREEN)
                .addInlineField("%s".formatted(army.getArmyType()), army.getName())
                .addInlineField("Faction", army.getFaction().getName())
                .addInlineField("Bound Player", boundPlayer == null ? "None" : boundPlayer.getIgn())
                .addField("Route", createPathStringWithCurrentRegion(movement.getPath(), army.getCurrentRegion()), false)
                .addInlineField("Duration", createDurationString(movement.getCost()))
                .addInlineField("Food Cost", foodCost.toString())
                .addInlineField("Faction Stockpile", army.getFaction().getFoodStockpile().toString())
                .setThumbnail(getFactionBanner(army.getFaction().getName()))
                .setTimestampToNow());
    }
}
