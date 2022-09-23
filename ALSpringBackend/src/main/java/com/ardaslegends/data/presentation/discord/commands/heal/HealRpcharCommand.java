package com.ardaslegends.data.presentation.discord.commands.heal;

import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.Thumbnails;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class HealRpcharCommand implements ALCommandExecutor {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /heal rpchar request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.trace("Building dto");
        DiscordIdDto dto = new DiscordIdDto(user.getIdAsString());
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling movementService");
        RPChar rpChar = discordServiceExecution(dto, playerService::healStart, "Error while starting RpChar Healing");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Started healing RpChar")
                .setDescription("The character %s - %s has started healing.\nThe healing takes 2 days.".formatted(rpChar.getName(), rpChar.getTitle()))
                .setColor(ALColor.GREEN)
                .addInlineField("Character", rpChar.getName())
                .addInlineField("User", user.getMentionTag())
                .addInlineField("Region", rpChar.getCurrentRegion().getId())
                .addInlineField("Duration", "2 days")
                .setThumbnail(Thumbnails.HEAL.getUrl())
                .setTimestampToNow();
    }
}
