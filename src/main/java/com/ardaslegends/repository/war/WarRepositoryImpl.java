package com.ardaslegends.repository.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.QWar;
import com.ardaslegends.domain.war.QWarParticipant;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class WarRepositoryImpl extends QuerydslRepositorySupport implements WarRepositoryCustom{

    public WarRepositoryImpl(){
        super(War.class);
    }

    public Set<War> queryWarsByFaction(Faction faction, boolean onlyActive) {
        Objects.requireNonNull(faction, "Faction must not be null!");
        QWar qWar = QWar.war;
        QWarParticipant qAggressors = QWarParticipant.warParticipant1;
        QWarParticipant qDefenders = QWarParticipant.warParticipant1;


        val result = from(qWar)
                .leftJoin(qWar.aggressors, qAggressors)
                .leftJoin(qWar.defenders, qDefenders)
                .where(
                        qAggressors.warParticipant.name.eq(faction.getName())
                        .or(qDefenders.warParticipant.name.eq(faction.getName()))
                        .and(qWar.isActive.eq(onlyActive)))
                .fetch();

        return new HashSet<>(result);
    }

    @Override
    public Set<War> queryWarsBetweenFactions(Faction faction1, Faction faction2, boolean onlyActive) {
        QWar qWar = QWar.war;
        QWarParticipant qAggressors = QWarParticipant.warParticipant1;
        QWarParticipant qDefenders = QWarParticipant.warParticipant1;

        val result = from(qWar)
                .leftJoin(qWar.aggressors, qAggressors)
                .leftJoin(qWar.defenders, qDefenders)
                .where(
                        qAggressors.warParticipant.name.eq(faction1.getName()).and(qDefenders.warParticipant.name.eq(faction2.getName()))
                        .or(qAggressors.warParticipant.name.eq(faction2.getName()).and(qDefenders.warParticipant.name.eq(faction1.getName())))
                        .and(qWar.isActive.eq(onlyActive)))
                .fetch();

        return new HashSet<>(result);
    }

    @Override
    public War findWarByAggressorsAndDefenders(Set<WarParticipant> aggressors,Set<WarParticipant> defenders) {
        //QWar war

        return new War();
    }
}
