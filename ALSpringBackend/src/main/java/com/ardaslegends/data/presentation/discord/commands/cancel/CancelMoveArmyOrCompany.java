package com.ardaslegends.data.presentation.discord.commands.cancel;

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
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
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
public class CancelMoveArmyOrCompany implements ALCommandExecutor {

    private final MovementService movementService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /cancel move army-or-company request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.debug("Getting options");
        String armyName = getStringOption("army-or-company-name", options);
        log.debug("army/company Name: [{}]", armyName);

        log.trace("Building dto");
        MoveArmyDto dto = new MoveArmyDto(user.getIdAsString(), armyName, null);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling movementService");
        Movement movement = discordServiceExecution(dto, movementService::cancelArmyMovement, "Error while cancelling Army/Company Movement");

        Army army = movement.getArmy();
        Player boundPlayer = army.getBoundTo();

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Cancelled %s Movement".formatted(army.getArmyType().getName()))
                .setDescription("The %s %s stopped its movement towards region %s."
                        .formatted(army.getArmyType().getName(), army.getName(), movement.getDestinationRegionId()))
                .setColor(ALColor.GREEN)
                .addInlineField("%s".formatted(army.getArmyType()), army.getName())
                .addInlineField("Faction", army.getFaction().getName())
                .addInlineField("Bound Player", boundPlayer == null ? "None" : boundPlayer.getIgn())
                .addField("Route", createPathStringWithCurrentRegion(movement.getPath(), army.getCurrentRegion()), false)
                .addField("Current Region", army.getCurrentRegion().getId(), false)
                .setThumbnail(getFactionBanner(army.getFaction().getName()))
                .setTimestampToNow();
    }
}
