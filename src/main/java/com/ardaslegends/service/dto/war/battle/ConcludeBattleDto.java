package com.ardaslegends.service.dto.war.battle;

import com.ardaslegends.service.dto.player.DiscordIdDto;
import com.ardaslegends.service.dto.war.SurvivingUnitsDto;

public record ConcludeBattleDto(
        Long battleId,
        String winnerFaction,
        SurvivingUnitsDto[] survivingUnits,
        DiscordIdDto[] playersKilled
) {
}
