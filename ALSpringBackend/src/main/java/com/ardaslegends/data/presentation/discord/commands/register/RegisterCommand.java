package com.ardaslegends.data.presentation.discord.commands.register;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.presentation.discord.commands.ALCommand;
import com.ardaslegends.data.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.data.presentation.discord.utils.ALColor;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.CreatePlayerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@Component
public class RegisterCommand implements ALCommand, ALCommandExecutor, DiscordUtils {

    private final DiscordApi api;
    private final PlayerService playerService;
    @Override
    public void init(Map<String, ALCommandExecutor> commands) {
        log.debug("Initializing /register command");

        SlashCommand register = SlashCommand.with("register", "JAVACORDDDDD Register in the roleplay system", Arrays.asList(
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("ign")
                        .setDescription("Your minecraft in-game name (IGN)")
                        .setRequired(true)
                        .build(),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("faction-name")
                        .setDescription("The faction you want to join")
                        .setRequired(true)
                        .build()
        ))
        .createGlobal(api)
        .join();

        commands.put("register", this::execute);
        log.info("Finished initializing /register command");
    }


    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction) {
        log.debug("Incoming /register request");

        String ign = getStringOption("ign", interaction);
        String factionName = getStringOption("faction-name", interaction);
        String discordId = interaction.getUser().getIdAsString();


        log.trace("Building dto");
        CreatePlayerDto dto = new CreatePlayerDto(ign, discordId, factionName);
        log.debug("Built dto with data [{}]", dto);

        log.trace("Calling playerService.createPlayer");
        Player player = discordServiceExecution(dto, playerService::createPlayer, "Error while registering!");

        log.debug("Building response Embed");
        return new EmbedBuilder()
                .setTitle("Registered")
                .setDescription("You were successfully registered in the Bot's system!")
                .addField("Ign", player.getIgn(), true)
                .addField("Faction", player.getFaction().getName(), true)
                .setColor(ALColor.GREEN)
                .setTimestampToNow();
    }
}
