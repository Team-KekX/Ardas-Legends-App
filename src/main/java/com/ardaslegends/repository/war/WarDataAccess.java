package com.ardaslegends.repository.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.AbstractDataAccess;
import com.ardaslegends.repository.exceptions.DataAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor

@Slf4j
@Repository
public class WarDataAccess extends AbstractDataAccess<War, WarRepository> {

    private final WarRepository warRepository;

    public Set<War> findAllWarsWithFaction(Faction faction) {
        log.debug("Finding all wars for faction {}", faction);
        requireParameterNonNull(faction, "faction", "findAllWarsWithFaction");

        return secureFind(faction, warRepository::findAllWarsWithFaction);
    }
}
