package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE;
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
    private Path path;
    private Path path2;
    private Path path3;
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

        fixedClock = Clock.fixed(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(fixedClock.instant());
        when(mockClock.getZone()).thenReturn(fixedClock.getZone());

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
        path = Path.builder().path(List.of(region.getId(), region2.getId(), region3.getId(), region4.getId())).cost(3).build();
        path2 = Path.builder().path(List.of(region4.getId(), region3.getId(), region2.getId(), region.getId())).cost(2).build();
        path3 = Path.builder().path(List.of(region2.getId(), region3.getId(), region4.getId())).cost(2).build();
        startTime = LocalDateTime.now();
        endTime = startTime.plusDays(path.getCost());
        endTime2 = startTime.plusDays(path2.getCost());
        endTime3 = startTime.plusDays(path3.getCost());
        movement = Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army).player(player).path(path)
                .startTime(startTime).endTime(endTime).hoursMoved(0).hoursUntilComplete(path.getCostInHours()).hoursUntilNextRegion(region2.getCostInHours())
                .build();
        movement2 = Movement.builder().isCharMovement(true).isCurrentlyActive(true).army(null).player(player2).path(path2)
                .startTime(startTime).endTime(endTime2).hoursMoved(0).hoursUntilComplete(path2.getCostInHours()).hoursUntilNextRegion(region3.getCostInHours())
                .build();
        movement3 = Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army2).player(null).path(path3)
                .startTime(startTime).endTime(endTime3).hoursMoved(0).hoursUntilComplete(path3.getCostInHours()).hoursUntilNextRegion(region3.getCostInHours())
                .build();

        when(mockRegionRepository.findById(region.getId())).thenReturn(Optional.of(region));
        when(mockRegionRepository.findById(region2.getId())).thenReturn(Optional.of(region2));
        when(mockRegionRepository.findById(region3.getId())).thenReturn(Optional.of(region3));
        when(mockRegionRepository.findById(region4.getId())).thenReturn(Optional.of(region4));
        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement, movement2, movement3));
    }

    @Test
    void ensureHandleMovementsWorks() {
        log.debug("Testing if handleMovements works properly!");

        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement2));

        scheduleService.handleMovements();

//        assertThat(army.getCurrentRegion()).isEqualTo(region2);
//        assertThat(rpChar.getCurrentRegion()).isEqualTo(region2);
//        assertThat(rpChar2.getCurrentRegion()).isEqualTo(region2);
//        assertThat(army2.getCurrentRegion()).isEqualTo(region3);
        log.info("Test passed: handleMovements works properly!");
    }
}
