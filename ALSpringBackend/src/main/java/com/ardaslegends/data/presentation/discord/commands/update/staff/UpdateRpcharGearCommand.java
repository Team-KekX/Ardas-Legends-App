package com.ardaslegends.data.presentation.discord.commands.update.staff;

import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class UpdateRpcharGearCommand implements ALStaffCommandExecutor {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /update rpchar gear request");

        checkStaff(interaction, properties.getStaffRoles());

        log.debug("Getting options");
        User user = getUserOption("player", options);
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        String gear = getStringOption("new-gear", options);
        log.debug("New-Gear: [{}]", gear);

        log.trace("Building dto");
        UpdateRpCharDto dto = new UpdateRpCharDto(user.getIdAsString(), null, null, null, null, gear, null);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService");
        RPChar rpChar = discordServiceExecution(dto, playerService::updateCharacterGear, "Error while updating Rp Char Gear");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Updated RpChar Gear")
                .setDescription("Successfully changed gear of Roleplay Character %s".formatted(rpChar.getName()))
                .addInlineField("Rp Char", rpChar.getName())
                .addInlineField("New Gear", rpChar.getGear())
                .addInlineField("User", user.getMentionTag())
                .setColor(ALColor.YELLOW)
                .setTimestampToNow();
    }
}
