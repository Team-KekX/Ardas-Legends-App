package com.ardaslegends.data.presentation.discord.commands;

import com.ardaslegends.data.presentation.discord.config.BotProperties;
import com.ardaslegends.data.presentation.discord.exception.BotException;
import com.ardaslegends.data.presentation.discord.utils.DiscordUtils;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public interface ALStaffCommand {

    Logger log = LoggerFactory.getLogger(DiscordUtils.class);

    default void checkStaff(SlashCommandInteraction interaction, List<String> staffRoles) {

        User user = interaction.getUser();
        Server server = interaction.getServer().get();
        log.debug("Checking if user [{}] is staff member of server [{}]",user.getName(), server.getName());
        log.debug("Staff roles are: [{}]", staffRoles);

        boolean isStaff = user.getRoles(server).stream()
                .map(Role::getIdAsString)
                .anyMatch(staffRoles::contains);

        if(!isStaff) {
            String message = "You are not a staff member and do not have the permission to execute this command!";
            throw new BotException("No permission to execute command", new RuntimeException(message));
        }

        log.debug("User is staff - continuing");
    }
}
