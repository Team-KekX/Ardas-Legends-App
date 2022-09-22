package com.ardaslegends.data.presentation.discord.commands.create.staff;

import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.commands.ALStaffCommand;
import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.rpchar.CreateRPCharDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;

@RequiredArgsConstructor

@Slf4j
public class CreateRpCharCommand implements ALCommandExecutor, ALStaffCommand, DiscordUtils {

    private final PlayerService playerService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Executing /create rpchar request");

        checkStaff(interaction, properties.getStaffRoles());

        String discordId = getUserOption("target", options).getIdAsString();
        String name = getStringOption("name", options);
        String title = getStringOption("title", options);
        String gear = getStringOption("gear", options);
        Boolean pvp = getBooleanOption("pvp", options);

        log.trace("Building dto");
        CreateRPCharDto dto = new CreateRPCharDto(discordId, name, title, gear, pvp);
        log.debug("Built dto with data [{}]", dto);

        RPChar rpChar = discordServiceExecution(dto, playerService::createRoleplayCharacter, "Error while creating Roleplay Character");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Created Roleplay Character")
                .setDescription("Successfully created Roleplay Character '%s - %s'!".formatted(rpChar.getName(), rpChar.getTitle()))
                .setColor(ALColor.YELLOW)
                .addField("Name", rpChar.getName(), true)
                .addField("Title", rpChar.getTitle(), true)
                .addField("Gear", rpChar.getGear(), true)
                .addField("PvP", pvp ? "Yes" : "No", true)
                .setTimestampToNow();
    }
}
