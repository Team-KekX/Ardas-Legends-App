package com.ardaslegends.presentation.discord.commands.info;

import com.ardaslegends.presentation.discord.commands.ALCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.ArmyService;
import com.ardaslegends.presentation.discord.utils.DiscordUtils;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor
public class InfoFactionUpkeepCommand implements ALCommandExecutor {

    private final ArmyService armyService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        DiscordUtils.log.debug("Incoming /info faction upkeep request");

        var factionName = getStringOption("faction-name", options);
        DiscordUtils.log.trace("InfoFactionUpkeep: faction name is [{}]", factionName);

        var result = discordServiceExecution(factionName, armyService::getUpkeepOfFaction, "Error while fetching upkeep value");
        DiscordUtils.log.trace("InfoFactionUpkeep: Result [{}]", result);

        DiscordUtils.log.debug("InfoFactionUpkeep: Building Embed");
        return new EmbedBuilder()
                .setTitle("%s's upkeep".formatted(factionName))
                .setThumbnail(getFactionBanner(factionName))
                .setDescription("Each army requires a monthly upkeep of 1000 coins. Thhe coins are used to provide food, weapons, armors and other things to the army")
                .addInlineField("Cost in Coins", result.upkeep().toString())
                .addInlineField("Number of armies", result.numberOfArmies().toString())
                .setColor(ALColor.GREEN);
    }
}
