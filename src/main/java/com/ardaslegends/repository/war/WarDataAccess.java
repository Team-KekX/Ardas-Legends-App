package com.ardaslegends.repository.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor

@Slf4j
@Repository
public class WarDataAccess {

    private final WarRepository warRepository;

    public Set<War> findAllWarsWithFaction(Faction faction) {
        log.debug("Finding all wars for faction {}", faction);
        Objects.requireNonNull(faction, "Faction must not be null!");

        //TODO add exception handling

        return warRepository.findAllWarsWithFaction(faction);
    }
}
