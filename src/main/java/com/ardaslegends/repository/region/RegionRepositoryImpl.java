package com.ardaslegends.repository.region;

import com.ardaslegends.domain.QRegion;
import com.ardaslegends.domain.Region;
import com.ardaslegends.repository.exceptions.RegionRepositoryException;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Objects;
import java.util.Optional;

public class RegionRepositoryImpl extends QuerydslRepositorySupport implements RegionRepositoryCustom {
    public RegionRepositoryImpl() {
        super(Region.class);
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
