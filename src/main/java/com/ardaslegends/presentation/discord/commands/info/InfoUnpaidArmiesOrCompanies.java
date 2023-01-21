package com.ardaslegends.presentation.discord.commands.info;

import com.ardaslegends.domain.Army;
import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.commands.ALMessageResponse;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.ArmyService;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InfoUnpaidArmiesOrCompanies implements ALCommandExecutor {

    private final ArmyService armyService;

    @Override
    public ALMessageResponse execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /info unpaid-armies-or-companies request");

        log.debug("UnpaidArmiesOrCompanies: Calling DiscordService");
        var armyList = discordServiceExecution(armyService::getUnpaid, "Error while getting unpaid armies");

        String armyNameString = armyList.stream()
                .map(Army::getName)
                .collect(Collectors.joining("\n"));
        String faction = armyList.stream()
                .map(army -> army.getFaction().getName())
                .collect(Collectors.joining("\n"));
        String createdAt = armyList.stream()
                        .map(army -> army.getCreatedAt().format(DateTimeFormatter.ISO_DATE))
                                .collect(Collectors.joining("\n"));

        log.debug("InfoUnpaidArmiesOrCompanies: Building Embed");
        return new ALMessageResponse(null, new EmbedBuilder()
                .setTitle("Unpaid armies")
                .setDescription("10 oldest armies that have not been paid for")
                .setColor(ALColor.GREEN)
                .setTimestampToNow()
                .addInlineField("Names", armyNameString)
                .addInlineField("Faction", faction)
                .addInlineField("Creation Date", createdAt));
    }
}
