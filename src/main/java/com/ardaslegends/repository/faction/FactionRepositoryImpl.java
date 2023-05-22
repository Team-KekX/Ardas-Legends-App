package com.ardaslegends.repository.faction;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.QFaction;
import com.ardaslegends.repository.exceptions.FactionRepositoryException;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Objects;
import java.util.Optional;

public class FactionRepositoryImpl extends QuerydslRepositorySupport implements FactionRepositoryCustom {

    public FactionRepositoryImpl() {
        super(Faction.class);
    }

    @Override
    public Faction queryByName(String factionName) {
        val fetchedFaction = queryByNameOptional(factionName);

        if(fetchedFaction.isEmpty()) {
            throw FactionRepositoryException.entityNotFound("factionName", factionName);
        }

        return fetchedFaction.get();
    }

    @Override
    public Optional<Faction> queryByNameOptional(String factionName) {
        Objects.requireNonNull(factionName);
        QFaction qFaction = QFaction.faction;

        Faction fetchedFaction = from(qFaction)
                .where(qFaction.name.equalsIgnoreCase(factionName)
                        .or(qFaction.aliases.any().equalsIgnoreCase(factionName)))
                .fetchFirst();

        return Optional.of(fetchedFaction);
    }
}
