package com.ardaslegends.service.discord;

import com.ardaslegends.presentation.discord.config.BotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class DiscordService {


    private final BotProperties botProperties;
    private final DiscordApi discordApi;

    public Optional<Role> getRoleById(Long roleId) {
        log.debug("Getting discord role with id [{}]", roleId);
        val foundRole = discordApi.getRoleById(roleId);

        if(foundRole.isPresent())
            log.debug("Found role [{}]", foundRole.get().getName());
        else
            log.debug("No role with id [{}] found", roleId);
        return foundRole;
    }
}
