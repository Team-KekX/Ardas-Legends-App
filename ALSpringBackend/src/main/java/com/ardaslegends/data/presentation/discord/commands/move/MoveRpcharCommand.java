package com.ardaslegends.data.presentation.discord.commands.move;

import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.Thumbnails;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
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
public class MoveRpcharCommand implements ALCommandExecutor {

    private final MovementService movementService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /move rpchar request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());


        log.debug("Getting options");
        String endRegion = getStringOption("end-region", options);
        log.debug("endRegion: [{}]", endRegion);

        log.trace("Building dto");
        MoveRpCharDto dto = new MoveRpCharDto(user.getIdAsString(), endRegion);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling movementService");
        Movement movement = discordServiceExecution(dto, movementService::createRpCharMovement, "Error while starting RpChar Movement");

        RPChar rpChar = movement.getPlayer().getRpChar();

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Started RpChar Movement")
                .setDescription("The character %s - %s has started their movement towards region %s".formatted(rpChar.getName(), rpChar.getTitle(), movement.getDestinationRegionId()))
                .setColor(ALColor.GREEN)
                .addInlineField("Character", rpChar.getName())
                .addInlineField("Player", movement.getPlayer().getIgn())
                .addInlineField("User", user.getMentionTag())
                .addField("Route", createPathString(movement.getPath()), false)
                .addField("Duration", createDurationString(movement.getCost()))
                .setThumbnail(Thumbnails.MOVE_CHARACTER.getUrl())
                .setTimestampToNow();
    }
}
