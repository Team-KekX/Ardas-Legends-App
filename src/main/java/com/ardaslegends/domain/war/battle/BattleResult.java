package com.ardaslegends.domain.war.battle;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.battle.UnitCasualty;
import jakarta.persistence.*;

import java.util.Set;

@Embeddable
public class BattleResult {

    @ManyToOne
    @JoinColumn(name = "winner_id", foreignKey = @ForeignKey(name = "fk_battle_result_winner_id"))
    private Faction winner;

    @ElementCollection
    @CollectionTable(name = "battle_unit_casualties",
            joinColumns = @JoinColumn(name = "battle_id", foreignKey = @ForeignKey(name = "fk_battle_unit_casualties_battle_id")))
    private Set<UnitCasualty> unitCasualties;

    @ElementCollection
    @CollectionTable(name = "battle_char_casualties",
            joinColumns = @JoinColumn(name = "battle_id", foreignKey = @ForeignKey(name = "fk_battle_char_casualties_battle_id")))
    private Set<UnitCasualty> rpCharCasualties;

}
