package com.ardaslegends.data.service.dto.claimbuilds;

import com.ardaslegends.data.domain.Army;

import java.util.List;

public record DeleteClaimbuildDto(String claimbuildName, List<String> unstationedArmies, List<String> deletedArmies){
}
