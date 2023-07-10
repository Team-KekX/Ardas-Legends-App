package com.ardaslegends.repository.region;

import com.ardaslegends.domain.QRegion;
import com.ardaslegends.domain.Region;
import com.ardaslegends.repository.exceptions.RegionRepositoryException;
import com.querydsl.core.types.OrderSpecifier;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class RegionRepositoryImpl extends QuerydslRepositorySupport implements RegionRepositoryCustom {
    public RegionRepositoryImpl() {
        super(Region.class);
    }

    @Override
    public Set<Region> queryAll() {
        log.debug("Querying all Regions in the database");
        QRegion qRegion = QRegion.region;

        val regions = from(qRegion)
                .orderBy(qRegion.id.desc())
                .fetch();

        return new HashSet<>(regions);
    }

    @Override
    public Region queryById(String id) {
        val fetchedRegion = queryByIdOptional(id);

        if(fetchedRegion.isEmpty()) {
            throw RegionRepositoryException.entityNotFound("id", id);
        }

        return fetchedRegion.get();
    }

    @Override
    public Optional<Region> queryByIdOptional(String id) {
        Objects.requireNonNull(id);

        QRegion qRegion = QRegion.region;

        val fetchedRegion = from(qRegion)
                .where(qRegion.id.equalsIgnoreCase(id))
                .fetchFirst();

        return Optional.ofNullable(fetchedRegion);
    }
}
