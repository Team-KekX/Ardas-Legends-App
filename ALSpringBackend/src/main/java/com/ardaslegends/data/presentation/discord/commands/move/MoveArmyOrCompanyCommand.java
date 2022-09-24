package com.ardaslegends.data.presentation.discord.commands.move;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.Thumbnails;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.dto.army.MoveArmyDto;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.utils.ServiceUtils;
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
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
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
        return new EmbedBuilder()
                .setTitle("Started %s Movement".formatted(army.getArmyType().getName()))
                .setDescription("The army %s has started its movement towards region %s.\n%d Stacks of food were removed from food stockpile of faction %s"
                        .formatted(army.getName(), movement.getDestinationRegionId(), foodCost, army.getFaction().getName()))
                .setColor(ALColor.GREEN)
                .addInlineField("Army", army.getName())
                .addInlineField("Faction", army.getFaction().getName())
                .addInlineField("Bound Player", boundPlayer == null ? "None" : boundPlayer.getIgn())
                .addField("Route", createPathString(movement.getPath()), false)
                .addInlineField("Duration", createDurationString(movement.getCost()))
                .addInlineField("Food Cost", foodCost.toString())
                .addInlineField("Faction Stockpile", army.getFaction().getFoodStockpile().toString())
                .setThumbnail(getFactionBanner(army.getFaction().getName()))
                .setTimestampToNow();
    }
}
