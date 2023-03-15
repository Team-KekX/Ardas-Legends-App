package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.domain.Player;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.UpdatePlayerIgnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class UpdatePlayerIgnCommand implements ALStaffCommandExecutor {


    private final PlayerService playerService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /update player ign request");

        checkStaff(interaction, properties.getStaffRoleIds());

        log.debug("Getting options");
        User user = getUserOption("player", options);
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        String ign = getStringOption("ign", options);
        log.debug("Ign: [{}]", ign);

        log.trace("Building dto");
        UpdatePlayerIgnDto dto = new UpdatePlayerIgnDto(ign, user.getIdAsString());
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService");
        Player player = discordServiceExecution(dto, playerService::updateIgn, "Error while updating Player Ign");

        log.debug("Building response Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Updated Player Ign")
                .setDescription("Successfully changed ign of Discord User %s to %s".formatted(user.getMentionTag(), player.getIgn()))
                .addInlineField("User", "%s".formatted(user.getMentionTag()))
                .addInlineField("New Ign", player.getIgn())
                .setColor(ALColor.YELLOW)
                .setTimestampToNow());
    }

}
