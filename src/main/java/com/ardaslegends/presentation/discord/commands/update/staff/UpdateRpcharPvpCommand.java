package com.ardaslegends.presentation.discord.commands.update.staff;

import com.ardaslegends.domain.RPChar;
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
public class UpdateRpcharPvpCommand implements ALStaffCommandExecutor {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /update rpchar pvp request");

        checkStaff(interaction, properties.getStaffRoles());

        log.debug("Getting options");
        User user = getUserOption("player", options);
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        Boolean pvp = getBooleanOption("new-pvp", options);
        log.debug("New-PvP: [{}]", pvp);

        log.trace("Building dto");
        UpdateRpCharDto dto = new UpdateRpCharDto(user.getIdAsString(), null, null, null, null, null, pvp);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService");
        RPChar rpChar = discordServiceExecution(dto, playerService::updateCharacterPvp, "Error while updating Rp Char PvP");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Updated RpChar PvP")
                .setDescription("Successfully updated PvP status of Character %s".formatted(rpChar.getName()))
                .setColor(ALColor.YELLOW)
                .addInlineField("Character", rpChar.getName())
                .addInlineField("New PvP", rpChar.getPvp().toString())
                .addInlineField("User", user.getMentionTag())
                .setTimestampToNow();
    }
}
