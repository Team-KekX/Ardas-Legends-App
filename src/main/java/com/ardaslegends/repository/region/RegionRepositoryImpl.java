package com.ardaslegends.repository.region;

import com.ardaslegends.domain.QRegion;
import com.ardaslegends.domain.Region;
import com.ardaslegends.repository.exceptions.RegionRepositoryException;
import com.querydsl.core.support.NumberConversion;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class RegionRepositoryImpl extends QuerydslRepositorySupport implements RegionRepositoryCustom {
    public RegionRepositoryImpl() {
        super(Region.class);
    }

    @Override
    public List<Region> queryAll() {
        log.debug("Querying all Regions in the database");
        QRegion qRegion = QRegion.region;

        return from(qRegion)
                .orderBy(qRegion.id.asc())
                .stream()
                .sorted((o1, o2) -> convertRegionIdToInt(o1.getId()) - convertRegionIdToInt(o2.getId()))
                .collect(Collectors.toList());
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

    private int convertRegionIdToInt(String regionId) {
        return regionId.contains(".") ? Integer.parseInt(regionId.split("\\.")[0]) + 10000 : Integer.parseInt(regionId);
    }
}
