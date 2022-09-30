package com.ardaslegends.service.dto.army;

public record UpdateArmyDto(String executorDiscordId, String armyName, Double freeTokens, Boolean isPaid) {
}
