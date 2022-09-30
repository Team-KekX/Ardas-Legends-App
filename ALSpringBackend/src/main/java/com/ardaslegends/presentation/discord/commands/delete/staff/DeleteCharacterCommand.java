package com.ardaslegends.presentation.discord.commands.delete.staff;

import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.PlayerService;
import com.ardaslegends.service.dto.player.DiscordIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Component
public class DeleteCharacterCommand implements ALStaffCommandExecutor {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /delete character request, getting option-data");

        checkStaff(interaction, properties.getStaffRoles());
        log.debug("User is a staff member -> allowed");

        User userId = getUserOption("target", options);
        log.trace("DeleteCharacter: User is [{}]", userId);

        DiscordIdDto dto = new DiscordIdDto(userId.getIdAsString());

        log.debug("Calling discordExecutionService");
        var character = discordServiceExecution(dto, playerService::deleteRpChar, "Error during deletion of the roleplay character!");

        return new EmbedBuilder()
                .setTitle("Deleted Roleplay Character")
                .setColor(ALColor.YELLOW)
                .setDescription("Deleted the character %s".formatted(character.getName()))
                .setTimestampToNow();
    }
}
