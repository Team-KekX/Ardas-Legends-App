package com.ardaslegends.presentation.discord.commands.cancel;

import com.ardaslegends.domain.RPChar;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.Thumbnails;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.DiscordIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class CancelHealRpcharCommand implements ALCommandExecutor {

    private final PlayerService playerService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /cancel heal rpchar request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.trace("Building dto");
        DiscordIdDto dto = new DiscordIdDto(user.getIdAsString());
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService");
        RPChar rpChar = discordServiceExecution(dto, playerService::healStop, "Error while cancelling RpChar Heal");

        log.debug("Building response Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Cancelled RpChar Heal")
                .setDescription("The character %s - %s stopped their healing.\nThe healing progress has been lost.".formatted(rpChar.getName(), rpChar.getTitle()))
                .setColor(ALColor.GREEN)
                .addInlineField("Character", rpChar.getName())
                .addInlineField("User", user.getMentionTag())
                .addInlineField("Current Region", rpChar.getCurrentRegion().getId())
                .setThumbnail(Thumbnails.HEAL.getUrl())
                .setTimestampToNow());
    }
}
