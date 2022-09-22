package com.ardaslegends.data.presentation.discord.commands.update.staff;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.ALStaffCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.UpdatePlayerFactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class UpdatePlayerFactionCommand implements ALCommandExecutor, ALStaffCommand, DiscordUtils {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /update player faction request");

        checkStaff(interaction, properties.getStaffRoles());

        log.debug("Getting options");
        String factionName = getStringOption("faction-name", options);
        log.debug("factionName: [{}]", factionName);
        User user = getUserOption("player", options);
        log.debug("Player: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.trace("Building dto");
        UpdatePlayerFactionDto dto = new UpdatePlayerFactionDto(user.getIdAsString(), factionName);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService");
        Player player = discordServiceExecution(dto, playerService::updatePlayerFaction, "Error while updating Player Faction");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Updated Player Faction")
                .setDescription("Player %s successfully changed to Faction %s!".formatted(player.getIgn(), player.getFaction().getName()))
                .setColor(ALColor.YELLOW)
                .addInlineField("Player", player.getIgn())
                .addInlineField("Faction", player.getFaction().getName())
                .setThumbnail(getFactionBanner(player.getFaction().getName()))
                .setTimestampToNow();
    }
}
