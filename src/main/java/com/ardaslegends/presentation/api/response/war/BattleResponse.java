package com.ardaslegends.presentation.api.response.war;

import com.ardaslegends.domain.war.Battle;

public record BattleResponse(
        String nameOfWar,
        String nameOfBattle,

        BattleArmyResponse[] attackingArmies,

        BattleArmyResponse[] defendingArmies

) {
    public BattleResponse(Battle battle) {
        this(
               battle.getWars().getName(),
               battle.getName(),

               battle.getAttackingArmies().stream()
                       .map(BattleArmyResponse::new)
                       .toArray(BattleArmyResponse[]::new),

                battle.getDefendingArmies().stream()
                        .map(BattleArmyResponse::new)
                        .toArray(BattleArmyResponse[]::new)
        );
    }
}
