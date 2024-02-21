package com.ardaslegends.domain.war.battle;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.battle.UnitCasualty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter

@Embeddable
public class BattleResult {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "winner_id", foreignKey = @ForeignKey(name = "fk_battle_result_winner_id"))
    private Faction winner;

    @ElementCollection
    @CollectionTable(name = "battle_unit_casualties",
            joinColumns = @JoinColumn(name = "battle_id", foreignKey = @ForeignKey(name = "fk_battle_unit_casualties_battle_id")))
    private Set<UnitCasualty> unitCasualties = new HashSet<>(2);

    @ElementCollection
    @CollectionTable(name = "battle_char_casualties",
            joinColumns = @JoinColumn(name = "battle_id", foreignKey = @ForeignKey(name = "fk_battle_char_casualties_battle_id")))
    private Set<UnitCasualty> rpCharCasualties = new HashSet<>(2);

    public BattleResult(Faction winner) {
        Objects.requireNonNull(winner, "BattleResult constructor: winner was null!");
        this.winner = winner;
    }

    public BattleResult(Faction winner, Set<UnitCasualty> unitCasualties, Set<UnitCasualty> rpCharCasualties) {
        Objects.requireNonNull(winner, "BattleResult constructor: winner was null!");
        this.winner = winner;
        this.unitCasualties = unitCasualties;
        this.rpCharCasualties = rpCharCasualties;
    }

    public Set<UnitCasualty> getUnitCasualties() { return Collections.unmodifiableSet(unitCasualties); }

    public Set<UnitCasualty> getRpCharCasualties() { return Collections.unmodifiableSet(rpCharCasualties); }

    @Override
    public String toString() {
        return "BattleResult{" +
                "winner=" + winner +
                ", unitCasualties=" + unitCasualties +
                ", rpCharCasualties=" + rpCharCasualties +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BattleResult that = (BattleResult) o;

        if (!winner.equals(that.winner)) return false;
        if (!Objects.equals(unitCasualties, that.unitCasualties))
            return false;
        return Objects.equals(rpCharCasualties, that.rpCharCasualties);
    }

    @Override
    public int hashCode() {
        int result = winner.hashCode();
        result = 31 * result + (unitCasualties != null ? unitCasualties.hashCode() : 0);
        result = 31 * result + (rpCharCasualties != null ? rpCharCasualties.hashCode() : 0);
        return result;
    }
}
