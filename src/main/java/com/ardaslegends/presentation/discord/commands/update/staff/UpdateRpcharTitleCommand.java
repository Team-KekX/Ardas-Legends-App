package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.domain.RPChar;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.rpchar.UpdateRpCharDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class UpdateRpcharTitleCommand implements ALStaffCommandExecutor {

    private final PlayerService playerService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /update rpchar title request");

        checkStaff(interaction, properties.getStaffRoleIds());

        log.debug("Getting options");
        User user = getUserOption("player", options);
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        String title = getStringOption("new-title", options);
        log.debug("New-title: [{}]", title);

        log.trace("Building dto");
        UpdateRpCharDto dto = new UpdateRpCharDto(user.getIdAsString(), null, title, null, null, null, null);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService");
        RPChar rpChar = discordServiceExecution(dto, playerService::updateCharacterTitle, "Error while updating Rp Char Title");

        log.debug("Building response Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Updated RpChar Title")
                .setDescription("Successfully updated Title of Character %s".formatted(rpChar.getName()))
                .setColor(ALColor.YELLOW)
                .addInlineField("Character", rpChar.getName())
                .addInlineField("New Title", rpChar.getTitle())
                .addInlineField("User", user.getMentionTag())
                .setTimestampToNow());
    }
}
