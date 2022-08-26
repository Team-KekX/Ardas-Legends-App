package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.exceptions.FactionServiceException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class FactionService extends AbstractService<Faction, FactionRepository>{

    private final FactionRepository factionRepository;

    public Faction getFactionByName(String name) {
        log.debug("Fetching Faction with name [{}]", name);
        Objects.requireNonNull(name, "Faction name must not be nulL!");

        if(name.isBlank()) {
            log.warn("Faction name is blank");
            throw new IllegalArgumentException("Faction name must not be blank!");
        }
        Optional<Faction> fetchedFaction = secureFind(name, factionRepository::findById);
        
        if (fetchedFaction.isEmpty()) {
            log.warn("No faction found with name {}", name);
            throw FactionServiceException.noFactionWithNameFound(name);
        }
        log.debug("Successfully fetched faction [{}]", fetchedFaction);

        return fetchedFaction.get();

    }


}
