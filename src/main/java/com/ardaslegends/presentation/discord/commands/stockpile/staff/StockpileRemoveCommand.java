package com.ardaslegends.presentation.discord.commands.stockpile.staff;

import com.ardaslegends.presentation.discord.commands.ALStaffCommandExecutor;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.FactionService;
import com.ardaslegends.service.dto.faction.UpdateStockpileDto;
import lombok.RequiredArgsConstructor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

@RequiredArgsConstructor
public class StockpileRemoveCommand implements ALStaffCommandExecutor {

    private final FactionService factionService;

    @Override
    public EmbedBuilder execute(SlashCommandInteraction interaction, List<SlashCommandInteractionOption> options, BotProperties properties) {
        log.debug("Incoming /stockpile remove request");

        checkStaff(interaction, properties.getStaffRoles());
        log.trace("StockpileRemove: User is staff -> allowed");


        var factionName = getStringOption("faction", options);
        log.trace("StockpileRemove: faction name is [{}]", factionName);

        var amount = getLongOption("amount", options).intValue();
        log.trace("StockpileRemove: amount is [{}]", amount);

        log.trace("StockpileRemove: Building Dto");
        UpdateStockpileDto dto = new UpdateStockpileDto(factionName, amount);

        log.debug("StockpileRemove: Calling discordService");
        var result = discordServiceExecution(dto, factionService::removeFromStockpile, "Error while updating stockpile");

        log.debug("StockpileRemove: Building Embed");
        return new EmbedBuilder()
                .setTitle("Removed from food stockpile")
                .setThumbnail(getFactionBanner(factionName))
                .setTimestampToNow()
                .setDescription("Removed %s stacks of food to %s's stockpile".formatted(amount, factionName))
                .addInlineField("Faction", result.getName())
                .addInlineField("Stockpile in Stacks", result.getFoodStockpile().toString())
                .setColor(ALColor.YELLOW);
    }

}
