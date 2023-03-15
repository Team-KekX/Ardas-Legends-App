package com.ardaslegends.presentation.discord.commands.declare;


import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.dto.war.CreateWarDto;
import com.ardaslegends.service.war.WarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Component
public class DeclareWarCommand implements ALCommandExecutor {

    private final WarService warService;
    private final DiscordApi api;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /declare war request");

        String executorDiscordId = interaction.getUser().getIdAsString();

        String warName = getStringOption("war-name", options);
        log.trace("war-name is -> {}", warName);

        String attackedFactionName = getStringOption("attacked-faction-name", options);
        log.trace("attacked-faction-name is -> {}", attackedFactionName);

        CreateWarDto warDto = new CreateWarDto(executorDiscordId, warName, attackedFactionName);

        War result = discordServiceExecution(warDto, warService::createWar, "Error while declaring war");

        Faction attacker = result.getAggressors().stream().findFirst().get().getWarParticipant();
        Faction defender= result.getDefenders().stream().findFirst().get().getWarParticipant();

        var attackerRole = attacker.getFactionRole();
        log.debug("Attacker role [{}]", attackerRole);
        var defenderRole = defender.getFactionRole();
        log.debug("Defender role [{}]", defenderRole);

        AllowedMentions mentions = new AllowedMentionsBuilder()
                .setMentionRoles(true)
                .build();

        MessageBuilder messageBuilder = new MessageBuilder()
                .setAllowedMentions(mentions)
                .append(attackerRole.getMentionTag())
                .append(" declared a War against")
                .append(defenderRole.getMentionTag())
                .append("!");


        log.debug("Title: [{}]", warName);
        log.debug("Attacker Name: [{}]", attacker.getName());

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(warName)
                .setDescription(messageBuilder.getStringBuilder().toString())
                .addInlineField("Attacker", attacker.getName())
                .addInlineField("Defender", defender.getName())
                .setColor(ALColor.GREEN)
                .setThumbnail(getFactionBanner(attacker.getName()))
                .setTimestampToNow();

        messageBuilder.setEmbed(embedBuilder);

        return new ALMessageResponse(messageBuilder, embedBuilder);
    }
}
