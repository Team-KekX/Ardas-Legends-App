package com.ardaslegends.presentation.api.response.army;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.Unit;
import com.ardaslegends.presentation.api.response.unit.UnitResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public record ArmyResponse(
        long id,
        String name,
        String armyType,
        String faction,
        String currentRegion,
        String boundTo,
        UnitResponse[] units,
        String[] sieges,
        String claimbuildName,
        Double freeTokens,
        Boolean isHealing,
        LocalDateTime healStart,
        LocalDateTime healEnd,
        Integer hoursHealed,
        Integer hoursLeftHealing,
        String originalClaimbuild,
        LocalDateTime createdAt

) {
    public ArmyResponse(Army army) {
        this(
                army.getId(),
                army.getName(),
                army.getArmyType().getName(),
                army.getFaction().getName(),
                army.getCurrentRegion().getId(),
                army.getBoundTo() == null ? null : army.getBoundTo().getName(),
                army.getUnits().stream().map(UnitResponse::new).toArray(UnitResponse[]::new),
                army.getSieges().toArray(String[]::new),
                army.getStationedAt() == null ? "null": army.getStationedAt().getName(),
                army.getFreeTokens(),
                army.getIsHealing(),
                army.getHealStart(),
                army.getHealEnd(),
                army.getHoursHealed(),
                army.getHoursLeftHealing(),
                army.getOriginalClaimbuild().getName(),
                army.getCreatedAt()
        );
    }
}
