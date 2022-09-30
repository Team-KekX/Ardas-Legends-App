package com.ardaslegends.service.dto.claimbuilds;

import java.util.List;

public record DeleteClaimbuildDto(String claimbuildName, List<String> unstationedArmies, List<String> deletedArmies){
}
