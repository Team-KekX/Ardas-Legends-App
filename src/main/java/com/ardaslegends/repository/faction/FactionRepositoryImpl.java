package com.ardaslegends.repository.faction;

import com.ardaslegends.domain.Faction;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class FactionRepositoryImpl extends QuerydslRepositorySupport implements FactionRepositoryCustom {

    public FactionRepositoryImpl() {
        super(Faction.class);
    }
}
