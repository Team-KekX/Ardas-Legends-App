package com.ardaslegends.service.dto.war;

public record CreateWarDto (
        String executorDiscordId,
        String nameOfWar,
        String defendingFactionName
) { }
