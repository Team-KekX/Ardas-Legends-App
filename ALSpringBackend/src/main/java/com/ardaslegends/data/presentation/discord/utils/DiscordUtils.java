package com.ardaslegends.data.presentation.discord.utils;

import com.ardaslegends.data.presentation.discord.exception.BotException;
import com.ardaslegends.data.presentation.exceptions.BadArgumentException;
import com.ardaslegends.data.presentation.exceptions.InternalServerException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.awt.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DiscordUtils {

    default String getFullCommandName(SlashCommandInteraction commandInteraction) {
        StringBuilder commandName = new StringBuilder(commandInteraction.getCommandName());

        SlashCommandInteractionOption option = commandInteraction.getOptions().get(0);

        while(option.isSubcommandOrGroup()) {
            commandName.append(" %s".formatted(option.getName()));
            option = option.getOptions().get(0);
        }

        return commandName.toString();
    }

    default EmbedBuilder createErrorEmbed(String title, String description) {
        return new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(ALColor.RED)
                .setTimestampToNow();
    }

    default String getStringOption(String name, SlashCommandInteraction interaction) {
        Optional<String> foundOption = interaction.getOptionStringValueByName(name);
        if(foundOption.isEmpty()) {
            throw new RuntimeException("No String option with name '%s' found!");
        }

        String option = foundOption.get().trim();
        return option;
    }

    default User getUserOption(String name, SlashCommandInteraction interaction) {
        Optional<User> foundOption = interaction.getOptionUserValueByName(name);
        if(foundOption.isEmpty()) {
            throw new RuntimeException("No User option with name '%s' found!");
        }

        return foundOption.get();
    }

    default Boolean getBooleanOption(String name, SlashCommandInteraction interaction) {
        Optional<Boolean> foundOption = interaction.getOptionBooleanValueByName(name);
        if(foundOption.isEmpty()) {
            throw new RuntimeException("No Boolean option with name '%s' found!");
        }

        return foundOption.get();
    }

    default <T, R> R discordServiceExecution(T argument, Function<T, R> function, String errorTitle) {
        if(function == null) {
            throw new InternalServerException("Passed Null SupplierFunction in discordServiceExecution", null);
        }
        try {
            return function.apply(argument);
        } catch (ServiceException e) {
            throw new BotException(errorTitle, e);
        }
    }

}
