package com.ardaslegends.repository.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;

import java.util.Set;

public interface WarRepositoryCustom {
    Set<War> queryWarsByFaction(Faction faction, boolean onlyActive);
    Set<War> queryWarsBetweenFactions(Faction faction1, Faction faction2, boolean onlyActive);
    War findWarByAggressorsAndDefenders(Set<WarParticipant> aggressors, Set<WarParticipant> defenders);

}
