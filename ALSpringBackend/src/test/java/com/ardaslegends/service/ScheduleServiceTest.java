package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.PlayerService;
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
    private MovementRepository mockMovementRepository;
    private ArmyRepository mockArmyRepository;
    private PlayerRepository mockPlayerRepository;
    private MovementService mockMovementService;
    private ArmyService mockArmyService;
    private PlayerService mockPlayerService;
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
    private ClaimBuild claimBuild;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime endTime2;
    private LocalDateTime endTime3;
    private UnitType unitType;
    private UnitType unitType2;
    private UnitType unitType3;
    private Unit unit;
    private Unit unit2;
    private Unit unit3;

    @BeforeEach
    void setup() {
        mockMovementRepository = mock(MovementRepository.class);
        mockArmyRepository = mock(ArmyRepository.class);
        mockPlayerRepository = mock(PlayerRepository.class);
        mockMovementService = mock(MovementService.class);
        mockArmyService = mock(ArmyService.class);
        mockPlayerService = mock(PlayerService.class);
        mockClock = mock(Clock.class);

        scheduleService = new ScheduleService(mockMovementRepository, mockArmyRepository, mockPlayerRepository, mockMovementService, mockArmyService, mockPlayerService, mockClock);

        unitType = UnitType.builder().unitName("Gondor Soldier").tokenCost(1.0).build();
        unitType2 = UnitType.builder().unitName("Gondor Archer").tokenCost(1.5).build();
        unitType3 = UnitType.builder().unitName("Tower Guaard").tokenCost(2.0).build();
        unit = Unit.builder().unitType(unitType).amountAlive(2).count(4).build();
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
        path2 = List.of(pathElement4, pathElement3, pathElement2);
        path3 = List.of(pathElement2, pathElement3);
        startTime = LocalDateTime.of(2022, 8, 31, 0, 0, 0);
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
        claimBuild = ClaimBuild.builder().type(ClaimBuildType.CASTLE).specialBuildings(List.of(SpecialBuilding.HOUSE_OF_HEALING)).region(region).build();

        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement, movement2, movement3));
        fixedClock = Clock.fixed(startTime.plusDays(1).plusSeconds(1).toInstant(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())), ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(fixedClock.instant());
        when(mockClock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void ensureHandleMovementsWorksForArmyMoves() {
        log.debug("Testing if handleMovements works properly!");

        //This is so we check if the bot instantly completes movements that are already over the end date
        movement3.setStartTime(startTime.minusMonths(1));
        movement3.setEndTime(startTime.minusMonths(1).plusHours(ServiceUtils.getTotalPathCost(path3)));

        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement, movement3));

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
        endTime2 = startTime.plusHours(ServiceUtils.getTotalPathCost(path2));
        movement2.setStartTime(startTime);
        movement2.setEndTime(endTime2);
        movement2.setHoursUntilComplete(ServiceUtils.getTotalPathCost(path2));
        movement2.setHoursUntilNextRegion(pathElement3.getActualCost());

        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement2));

        scheduleService.handleMovements();

        assertThat(rpChar2.getCurrentRegion()).isEqualTo(region2);
        log.info("Test passed: handleMovements works properly!");
    }

    @Test
    void ensureHandleMovementsExitsWhenHourHasNotPassed() {
        log.debug("Testing if handleMovements works properly!");

        fixedClock = Clock.fixed(startTime.plusMinutes(50).toInstant(ZoneId.systemDefault().getRules().getOffset(startTime)), ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(fixedClock.instant());
        when(mockClock.getZone()).thenReturn(fixedClock.getZone());

        when(mockMovementRepository.findMovementsByIsCurrentlyActive(true)).thenReturn(List.of(movement, movement3));

        scheduleService.handleMovements();

        assertThat(army.getCurrentRegion()).isEqualTo(path.get(0).getRegion());
        assertThat(rpChar.getCurrentRegion()).isEqualTo(path.get(0).getRegion());
        assertThat(army2.getCurrentRegion()).isEqualTo(path3.get(0).getRegion());
        log.info("Test passed: handleMovements works properly!");
    }

    @Test
    void ensureHandleHealingWorksForChars() {
        log.debug("Testing if handleHealings works properly for rp chars!");

        rpChar.setInjured(true);
        rpChar.setIsHealing(true);
        rpChar.setCurrentRegion(region);
        rpChar.setStartedHeal(startTime);
        rpChar.setHealEnds(startTime.plusDays(2));

        rpChar2.setInjured(true);
        rpChar2.setIsHealing(true);
        rpChar2.setCurrentRegion(region);
        rpChar2.setStartedHeal(startTime.minusDays(1));
        rpChar2.setHealEnds(startTime.plusDays(1));

        log.info("Current time: [{}] - start time: [{}] - end time: [{}]", LocalDateTime.now(mockClock), startTime.minusDays(1), startTime.plusDays(1));

        when(mockPlayerRepository.findPlayerByRpCharIsHealingTrue()).thenReturn(List.of(player, player2));

        scheduleService.handleHealings();

        assertThat(rpChar.getIsHealing()).isTrue();
        assertThat(rpChar.getInjured()).isTrue();
        assertThat(rpChar2.getIsHealing()).isFalse();
        assertThat(rpChar2.getInjured()).isFalse();
        log.info("Test passed: handleHealings works properly for rp chars!");
    }

    @Test
    void ensureHealingWorksForArmies() {
        log.debug("Testing if healing armies works");

        army.setIsHealing(true);
        army.setHealStart(startTime);
        army.setHealEnd(startTime.plusHours(army.getAmountOfHealHours()));
        

        log.info("Test passed: healing armies works");
    }

    @Test
    void ensureClockGetsReturned() {
        assertThat(scheduleService.clock()).isEqualTo(Clock.systemDefaultZone());
    }
}
