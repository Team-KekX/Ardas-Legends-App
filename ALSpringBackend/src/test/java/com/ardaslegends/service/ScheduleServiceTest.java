package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.ScheduleService;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ScheduleServiceTest {

    private ScheduleService scheduleService;
    private RegionRepository mockRegionRepository;
    private MovementRepository mockMovementRepository;
    private MovementService mockMovementService;
    private Clock mockClock;
    private Clock fixedClock;

    private Player player;
    private Player player2;
    private RPChar rpChar;
    private RPChar rpChar2;
    private Army army;
    private Army army2;
    private Region region;
    private Region region2;
    private Region region3;
    private Region region4;
    private PathElement pathElement;
    private PathElement pathElement2;
    private PathElement pathElement3;
    private PathElement pathElement4;
    private List<PathElement> path;
    private List<PathElement> path2;
    private List<PathElement> path3;
    private Movement movement;
    private Movement movement2;
    private Movement movement3;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime endTime2;
    private LocalDateTime endTime3;

    @BeforeEach
    void setup() {
        mockRegionRepository = mock(RegionRepository.class);
        mockMovementRepository = mock(MovementRepository.class);
        mockMovementService = mock(MovementService.class);
        mockClock = mock(Clock.class);

        scheduleService = new ScheduleService(mockMovementRepository, mockMovementService, mockRegionRepository, mockClock);

        region = Region.builder().id("91").regionType(RegionType.LAND).build();
        region2 = Region.builder().id("92").regionType(RegionType.LAND).build();
        region3 = Region.builder().id("93").regionType(RegionType.LAND).build();
        region4 = Region.builder().id("94").regionType(RegionType.LAND).build();
        rpChar = RPChar.builder().name("Belegorn").boundTo(army).currentRegion(region).build();
        rpChar2 = RPChar.builder().name("Tinwe").currentRegion(region4).build();
        player = Player.builder().rpChar(rpChar).build();
        player2 = Player.builder().rpChar(rpChar2).build();
        army = Army.builder().name("Knights of Gondor").currentRegion(region).boundTo(player).build();
        army2 = Army.builder().name("Gondor Army").currentRegion(region2).boundTo(null).build();
        pathElement = PathElement.builder().region(region).actualCost(region.getCost()).baseCost(region.getCost()).build();
        pathElement2 = PathElement.builder().region(region2).actualCost(region2.getCost()).baseCost(region2.getCost()).build();
        pathElement3 = PathElement.builder().region(region3).actualCost(region3.getCost()).baseCost(region3.getCost()).build();
        pathElement4 = PathElement.builder().region(region4).actualCost(region4.getCost()).baseCost(region4.getCost()).build();
        path = List.of(pathElement, pathElement2, pathElement3, pathElement4);
        path2 = List.of(pathElement4, pathElement3, pathElement2, pathElement);
        path3 = List.of(pathElement2, pathElement3, pathElement4);
        startTime = LocalDateTime.now();
        endTime = startTime.plusHours(ServiceUtils.getTotalPathCost(path));
        endTime2 = startTime.plusHours(ServiceUtils.getTotalPathCost(path2));
        endTime3 = startTime.plusHours(ServiceUtils.getTotalPathCost(path3));
        movement = Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army).player(player).path(path)
                .startTime(startTime).endTime(endTime).hoursMoved(0).hoursUntilComplete(ServiceUtils.getTotalPathCost(path)).hoursUntilNextRegion(path.get(1).getActualCost())
                .build();
        movement2 = Movement.builder().isCharMovement(true).isCurrentlyActive(true).army(null).player(player2).path(path2)
                .startTime(startTime).endTime(endTime2).hoursMoved(0).hoursUntilComplete(ServiceUtils.getTotalPathCost(path2)).hoursUntilNextRegion(path2.get(1).getActualCost())
                .build();
        movement3 = Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army2).player(null).path(path3)
                .startTime(startTime).endTime(endTime3).hoursMoved(0).hoursUntilComplete(ServiceUtils.getTotalPathCost(path3)).hoursUntilNextRegion(path3.get(1).getActualCost())
                .build();

        when(mockRegionRepository.findById(region.getId())).thenReturn(Optional.of(region));
        when(mockRegionRepository.findById(region2.getId())).thenReturn(Optional.of(region2));
        when(mockRegionRepository.findById(region3.getId())).thenReturn(Optional.of(region3));
        when(mockRegionRepository.findById(region4.getId())).thenReturn(Optional.of(region4));
        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement, movement2, movement3));

        fixedClock = Clock.fixed(LocalDateTime.now().plusDays(1).plusHours(1).toInstant(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())), ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(fixedClock.instant());
        when(mockClock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void ensureHandleMovementsWorksForArmyMoves() {
        log.debug("Testing if handleMovements works properly!");

        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement, movement3));

        log.trace("Now: [{}]", LocalDateTime.now());
        log.trace("Clock: [{}]", LocalDateTime.now(mockClock));

        scheduleService.handleMovements();

        assertThat(army.getCurrentRegion()).isEqualTo(region2);
        assertThat(rpChar.getCurrentRegion()).isEqualTo(region2);
        assertThat(army2.getCurrentRegion()).isEqualTo(region3);
        log.info("Test passed: handleMovements works properly!");
    }

    @Test
    void ensureHandleMovementsWorksForCharMoves() {
        log.debug("Testing if handleMovements works properly!");

        pathElement.setActualCost(pathElement.getActualCost()/2);
        pathElement2.setActualCost(pathElement2.getActualCost()/2);
        pathElement3.setActualCost(pathElement3.getActualCost()/2);
        pathElement4.setActualCost(pathElement4.getActualCost()/2);
        log.trace("Total path cost for path 2: [{}]", ServiceUtils.getTotalPathCost(path2));
        endTime2 = startTime.plusHours(ServiceUtils.getTotalPathCost(path2));
        movement2.setStartTime(startTime);
        movement2.setEndTime(endTime2);
        movement2.setHoursUntilComplete(ServiceUtils.getTotalPathCost(path2));
        movement2.setHoursUntilNextRegion(pathElement3.getActualCost());

        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement2));

        log.trace("Now: [{}]", LocalDateTime.now());
        log.trace("Clock: [{}]", LocalDateTime.now(mockClock));

        scheduleService.handleMovements();

        assertThat(rpChar2.getCurrentRegion()).isEqualTo(region2);
        log.info("Test passed: handleMovements works properly!");
    }
}
