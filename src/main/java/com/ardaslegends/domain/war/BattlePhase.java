package com.ardaslegends.domain.war;

public enum BattlePhase {
    /**
     * after declaration, before the 24h timer runs out
     * during this phase allies can aid
     */
    PRE_BATTLE,
    /**
     * during time freeze, waiting for battle to happen
     */
    ONGOING,
    /**
     * after the battle happened and results are submitted
     */
    CONCLUDED
}
