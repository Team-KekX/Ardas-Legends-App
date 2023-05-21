package com.ardaslegends.repository.region;

import com.ardaslegends.domain.Region;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class RegionRepositoryImpl extends QuerydslRepositorySupport implements RegionRepositoryCustom {
    public RegionRepositoryImpl() {
        super(Region.class);
    }
}
