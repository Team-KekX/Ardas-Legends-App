package com.ardaslegends.repository.faction;

import com.ardaslegends.domain.Faction;

import java.util.Optional;

public interface FactionRepositoryCustom {
    Faction queryByName(String factionName);
    Optional<Faction> queryByNameOptional(String factionName);
}
