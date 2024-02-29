package com.ardaslegends.repository;

import com.ardaslegends.domain.QUnitType;
import com.ardaslegends.domain.UnitType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

@Slf4j
public class UnitTypeRepositoryImpl extends QuerydslRepositorySupport implements UnitTypeRepositoryCustom{
    public UnitTypeRepositoryImpl(){
        super(UnitType.class);
    }
    @Override
    public Set<UnitType> queryByFactionNames(List<String> factionNames) {
        log.debug("Querying UnitTypes by faction names: [{}]", StringUtils.join(factionNames, ", "));

        QUnitType unitType = QUnitType.unitType;


        return null;
    }
}
