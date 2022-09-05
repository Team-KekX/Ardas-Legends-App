package com.ardaslegends.data.presentation.discord.commands;

import com.ardaslegends.data.presentation.discord.exception.BotException;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Arrays;
import java.util.List;

public interface ALStaffCommand {

    default void checkStaff(User user, Server server, List<String> staffRoles) {

        boolean isStaff = user.getRoles(server).stream().map(Role::getIdAsString).anyMatch(staffRoles::contains);

        if(!isStaff) {
            String message = "You are not a staff member and do not have the permission to execute this command!";
            throw new BotException("No permission to execute command", new RuntimeException(message));
        }
    }
}
