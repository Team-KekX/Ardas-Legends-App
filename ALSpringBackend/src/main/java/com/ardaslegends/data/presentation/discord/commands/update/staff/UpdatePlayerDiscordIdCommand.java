package com.ardaslegends.data.presentation.discord.commands.update.staff;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.ALStaffCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import com.ardaslegends.data.service.dto.player.UpdateDiscordIdDto;
import com.ardaslegends.data.service.dto.player.UpdatePlayerFactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class UpdatePlayerDiscordIdCommand implements ALCommandExecutor, ALStaffCommand, DiscordUtils {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /update player discord-id request");

        checkStaff(interaction, properties.getStaffRoles());

        log.debug("Getting options");
        String oldDiscordId = getStringOption("old-discord-id", options);
        log.debug("oldDiscordId: [{}]", oldDiscordId);
        String newDiscordId = getStringOption("new-discord-id", options);
        log.debug("newDiscordId: [{}]", newDiscordId);

        log.trace("Building dto");
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto(oldDiscordId, newDiscordId);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService");
        Player player = discordServiceExecution(dto, playerService::updateDiscordId, "Error while updating Discord Id");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Updated Discord ID")
                .setDescription("Updated the Discord ID of player %s".formatted(player.getIgn()))
                .setColor(ALColor.YELLOW)
                .addInlineField("Player IGN", player.getIgn())
                .addInlineField("Old Discord ID", oldDiscordId)
                .addInlineField("New Discord ID", newDiscordId)
                .setTimestampToNow();
    }
}
