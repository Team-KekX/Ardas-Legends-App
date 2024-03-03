package com.ardaslegends.repository;

import com.ardaslegends.domain.UnitType;

import java.util.List;

public interface UnitTypeRepositoryCustom {

    List<UnitType> queryByFactionNames(List<String> factionNames);
}
