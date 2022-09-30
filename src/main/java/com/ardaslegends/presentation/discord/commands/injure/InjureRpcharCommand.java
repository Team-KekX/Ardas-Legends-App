package com.ardaslegends.presentation.discord.commands.injure;

import com.ardaslegends.domain.RPChar;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
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
public class InjureRpcharCommand implements ALCommandExecutor {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /injure rpchar request");

        User user = interaction.getUser();
        log.debug("User: discord name [{}] - id [{}]", user.getName(), user.getIdAsString());

        log.trace("Building dto");
        DiscordIdDto dto = new DiscordIdDto(user.getIdAsString());
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling movementService");
        RPChar rpChar = discordServiceExecution(dto, playerService::injureChar, "Error while injuring RpChar");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Injured RpChar")
                .setDescription("The character %s - %s has been injured.\nThey cannot bind to armies anymore and have possibly been unbound from their last bound army.".formatted(rpChar.getName(), rpChar.getTitle()))
                .setColor(ALColor.GREEN)
                .addInlineField("Character", rpChar.getName())
                .addInlineField("User", user.getMentionTag())
                .setThumbnail(Thumbnails.INJURE_CHARACTER.getUrl())
                .setTimestampToNow();
    }
}
