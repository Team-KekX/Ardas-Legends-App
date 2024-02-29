package com.ardaslegends.repository;

import com.ardaslegends.domain.UnitType;

import java.util.List;
import java.util.Set;

public interface UnitTypeRepositoryCustom {

    Set<UnitType> queryByFactionNames(List<String> factionNames);
}
