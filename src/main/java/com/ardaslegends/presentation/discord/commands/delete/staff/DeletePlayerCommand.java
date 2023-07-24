package com.ardaslegends.presentation.discord.commands.delete.staff;

import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.DiscordIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Component
public class DeletePlayerCommand implements ALStaffCommandExecutor {

    private final PlayerService playerService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Handling /delete player request, fetchign data");

        checkStaff(interaction, properties.getStaffRoleIds());
        log.debug("DeletePlayer: Is Staff");

        String userId = getStringOption("target-player-id", options);
        log.trace("DeletePlayer: Id of player is [{}]", userId);

        log.trace("Building DiscordId Dto");
        DiscordIdDto dto = new DiscordIdDto(userId);

        log.trace("DeletePlayer: Calling ServiceExecution");
        var player = discordServiceExecution(dto, playerService::deletePlayer, "Error while deleting Player!");

        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Staff-Deleted Player")
                .setColor(ALColor.YELLOW)
                .setDescription("Staff Deleted Player with ign '%s'".formatted(player.getIgn()))
                .setTimestampToNow());
    }
}
