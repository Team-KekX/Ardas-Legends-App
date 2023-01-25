package com.ardaslegends.presentation.api.response.war;

import io.swagger.v3.oas.annotations.media.Schema;

public record BattleResponse(
        String nameOfWar,
        String nameOfBattle,

        BattleArmyResponse[] attackingArmies,

        BattleArmyResponse[] defendingArmies

) {
}
