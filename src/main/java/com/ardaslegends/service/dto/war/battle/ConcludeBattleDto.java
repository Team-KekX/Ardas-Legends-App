package com.ardaslegends.service.dto.war.battle;

import com.ardaslegends.service.dto.player.DiscordIdDto;

public record ConcludeBattleDto(
        Long battleId,
        String winnerFaction,
        SurvivingUnitsDto[] survivingUnits,
        DiscordIdDto[] playersKilled
) {
}
