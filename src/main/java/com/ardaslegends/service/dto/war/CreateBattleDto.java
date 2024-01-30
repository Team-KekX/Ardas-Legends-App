package com.ardaslegends.service.dto.war;

public record CreateBattleDto(
        String executorDiscordId,
        String battleName,
        String attackingArmyName,
        String defendingArmyName,
        boolean isFieldBattle,
        String claimBuildName
) {
}
