package com.ardaslegends.presentation.api;

import com.ardaslegends.domain.Region;
import com.ardaslegends.presentation.AbstractRestController;
import com.ardaslegends.repository.FactionRepository;
import com.ardaslegends.repository.RegionRepository;
import com.ardaslegends.repository.UnitTypeRepository;
import com.ardaslegends.service.*;
import com.ardaslegends.service.war.WarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping(TestDataController.BASE_URL)
public class TestDataController extends AbstractRestController {

    public static final String BASE_URL = "/api/testdata";

    private final UnitTypeService unitTypeService;
    private final UnitTypeRepository unitTypeRepository;

    private final RegionRepository regionRepository;
    private final PlayerService playerService;
    private final WarService warService;
    private final ArmyService armyService;

    private final MovementService movementService;
    private final ClaimBuildService claimBuildService;
    private final FactionService factionService;
    private final FactionRepository factionRepository;


    @PostMapping
    public void generateTestData() {

        Set<Region> regionSet = new HashSet<>(50);

        IntStream.range(1, 51)
                .forEach(value -> new Region());

    }

    private String randomStringNoNumbers() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private String randomStringWithNumbers() {
        return RandomStringUtils.random(15, true, true);
    }

}
