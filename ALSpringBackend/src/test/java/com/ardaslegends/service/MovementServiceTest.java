package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.Pathfinder;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.army.MoveArmyDto;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.exceptions.PlayerServiceException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.service.exceptions.movement.MovementServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class MovementServiceTest {

    private MovementRepository mockMovementRepository;
    private RegionRepository mockRegionRepository;
    private ArmyRepository mockArmyRepository;
    private ArmyService mockArmyService;
    private PlayerRepository mockPlayerRepository;
    private PlayerService mockPlayerService;
    private Pathfinder mockPathfinder;

    private MovementService movementService;

    private Faction faction;
    private Region region1;
    private Region region2;
    private RPChar rpchar;
    private Player player;
    private UnitType unitType;
    private Unit unit;
    private Army army;
    private Movement movement;
    private ClaimBuild claimBuild;

    @BeforeEach
    void setup() {
        mockMovementRepository = mock(MovementRepository.class);
        mockRegionRepository = mock(RegionRepository.class);
        mockPlayerRepository = mock(PlayerRepository.class);
        mockPlayerService = mock(PlayerService.class);
        mockArmyRepository = mock(ArmyRepository.class);
        mockArmyService = mock(ArmyService.class);
        mockPathfinder = mock(Pathfinder.class);
        movementService = new MovementService(mockMovementRepository, mockRegionRepository, mockArmyRepository, mockArmyService, mockPlayerRepository, mockPlayerService, mockPathfinder);

        region1 = Region.builder().id("90").build();
        region2 = Region.builder().id("91").build();
        unitType = UnitType.builder().unitName("Gondor Archer").tokenCost(1.5).build();
        unit = Unit.builder().unitType(unitType).army(army).amountAlive(5).count(10).build();
        faction = Faction.builder().name("Gondor").allies(new ArrayList<>()).build();
        claimBuild = ClaimBuild.builder().name("Nimheria").siege("Ram, Trebuchet, Tower").region(region1).ownedBy(faction).specialBuildings(List.of(SpecialBuilding.HOUSE_OF_HEALING)).stationedArmies(List.of()).build();
        rpchar = RPChar.builder().name("Belegorn").isHealing(false).currentRegion(region1).build();
        player = Player.builder().discordID("1234").faction(faction).rpChar(rpchar).build();
        army = Army.builder().name("Knights of Gondor").armyType(ArmyType.ARMY).faction(faction).freeTokens(30 - unit.getCount() * unitType.getTokenCost()).currentRegion(region1).boundTo(player).stationedAt(claimBuild).sieges(new ArrayList<>()).createdAt(LocalDateTime.now().minusDays(3)).build();
        Path path = Path.builder().path(List.of("90","91")).cost(10).build();
        movement =  Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army).path(path).build();

        when(mockPlayerService.getPlayerByDiscordId(player.getDiscordID())).thenReturn(player);
        when(mockArmyRepository.findArmyByName(army.getName())).thenReturn(Optional.of(army));
        when(mockArmyService.getArmyByName(army.getName())).thenReturn(army);
        when(mockArmyRepository.save(army)).thenReturn(army);
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.of(movement));
        when(mockRegionRepository.findById(region1.getId())).thenReturn(Optional.of(region1));
        when(mockRegionRepository.findById(region2.getId())).thenReturn(Optional.of(region2));
        when(mockPathfinder.findShortestWay(any(),any(),any(),anyBoolean())).thenReturn(movement.getPath());
    }

    @Test
    void ensureMoveRpCharWorks() {
        log.debug("Testing if createRpCharMovement works with valid values!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").isHealing(false).currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();
        Path endPath = Path.builder().path(List.of("91", "92")).cost(2).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));
        when(mockRegionRepository.findById(toRegion.getId())).thenReturn(Optional.of(toRegion));
        when(mockPathfinder.findShortestWay(fromRegion, toRegion, player, true)).thenReturn(endPath);
        when(mockMovementRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        //Act
        Movement createdMovement = movementService.createRpCharMovement(dto);

        //Assert
        log.debug("Starting asserts");
        assertThat(createdMovement.getPlayer()).isEqualTo(player);
        assertThat(createdMovement.getIsCharMovement()).isTrue();
        assertThat(createdMovement.getStartTime().toLocalDate()).isEqualTo(LocalDate.now());
        assertThat(createdMovement.getEndTime().toLocalDate()).isEqualTo(LocalDate.now().plusDays(endPath.getCost()));
        assertThat(createdMovement.getIsAccepted()).isFalse();
        assertThat(createdMovement.getArmy()).isNull();

        log.info("Test passed: createRpCharMovement works with valid values!");
    }

    @Test
    void ensureMoveRpCharThrowsIAEWhenNoPlayerFound() {
        log.debug("Testing if createRpCharMovement throws IllegalArgumentException when no player is found!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region toRegion = Region.builder().id("92").build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.empty());

        //Act
        var exception = assertThrows(IllegalArgumentException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        assertThat(exception.getMessage()).contains("No player found");

        log.info("Test passed: createRpCharMovement throws IllegalArgumentException when no player is found!");
    }
    @Test
    void ensureMoveRpCharThrowsSEWhenNoRpChar() {
        log.debug("Testing if createRpCharMovement throws IllegalArgumentException when player has no Rp Char!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(null).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));

        //Act
        var exception = assertThrows(PlayerServiceException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        assertThat(exception.getMessage()).isEqualTo(PlayerServiceException.noRpChar().getMessage());

        log.info("Test passed: createRpCharMovement throws IllegalArgumentException when player has no Rp Char!");
    }


    @Test
    void ensureMoveRpCharThrowsIAEWhenEndRegionDoesNotExists() {
        log.debug("Testing if createRpCharMovement throws IllegalArgumentException when end Region does not exist!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").isHealing(false).currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));
        when(mockRegionRepository.findById(toRegion.getId())).thenReturn(Optional.empty());

        //Act
        var exception = assertThrows(IllegalArgumentException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        assertThat(exception.getMessage()).isEqualTo("The region %s does not exist!".formatted(dto.toRegion()));

        log.info("Test passed: createRpCharMovement throws IllegalArgumentException when player has no Rp Char!");
    }

    @Test
    void ensureMoveRpCharThrowsSEWhenBoundToArmy() {
        log.debug("Testing if createRpCharMovement throws ServiceException when Char is bound to Army!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();
        Army army = Army.builder().name("Army of Gondor").boundTo(player).currentRegion(fromRegion).build();
        rpChar.setBoundTo(army);

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));
        when(mockRegionRepository.findById(toRegion.getId())).thenReturn(Optional.empty());

        //Act
        var exception = assertThrows(ServiceException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        log.debug("Asserting that createRpCharMovement throws ServiceException");
        assertThat(exception.getMessage()).isEqualTo(ServiceException.cannotMoveRpCharBoundToArmy(rpChar, army).getMessage());

        log.info("Test passed: createRpCharMovement throws ServiceException when Char is bound to Army!");
    }

    @Test
    void ensureMoveRpCharThrowsSEWhenAlreadyInRegion() {
        log.debug("Testing if createRpCharMovement throws ServiceException when Char is already in destination region!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", fromRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));
        when(mockRegionRepository.findById(fromRegion.getId())).thenReturn(Optional.of(fromRegion));

        //Act
        var exception = assertThrows(ServiceException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        log.debug("Asserting that createRpCharMovement throws ServiceException");
        assertThat(exception.getMessage()).isEqualTo(ServiceException.cannotMoveRpCharAlreadyInRegion(rpChar, fromRegion).getMessage());

        log.info("Test passed: createRpCharMovement throws ServiceException when Char is already in destination region!");
    }

    @Test
    void ensureMoveRpCharThrowsSEWhenCharIsHealing() {
        log.debug("Testing if createRpCharMovement throws ServiceException when Char is healing!");

        // Assign
        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto(player.getDiscordID(), region2.getId());
        rpchar.setIsHealing(true);

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID(player.getDiscordID())).thenReturn(Optional.of(player));
        when(mockRegionRepository.findById(region2.getId())).thenReturn(Optional.of(region2));

        //Act
        var exception = assertThrows(MovementServiceException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        log.debug("Asserting that createRpCharMovement throws ServiceException");
        assertThat(exception.getMessage()).isEqualTo(MovementServiceException.cannotMoveCharIsHealing(rpchar.getName()).getMessage());

        log.info("Test passed: createRpCharMovement throws ServiceException when Char is healing!");
    }

    @Test
    void ensureMoveRpCharThrowsSEWhenCharAlreadyMoving() {
        log.debug("Testing if createRpCharMovement throws ServiceException when Char is already in a movement!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("90").build();
        Region toRegion = Region.builder().id("92").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").isHealing(false).currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();
        Movement movement = Movement.builder().isCharMovement(true).isAccepted(false)
                .startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).isCurrentlyActive(true).player(player).build();

        log.trace("Initializing Dto");
        MoveRpCharDto dto = new MoveRpCharDto("1234", toRegion.getId());

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));
        when(mockRegionRepository.findById(toRegion.getId())).thenReturn(Optional.of(toRegion));
        when(mockMovementRepository.findMovementsByPlayer(player)).thenReturn(List.of(movement));

        //Act
        var exception = assertThrows(ServiceException.class, () -> movementService.createRpCharMovement(dto));

        //Assert
        log.debug("Asserting that createRpCharMovement throws ServiceException");
        assertThat(exception.getMessage()).isEqualTo(ServiceException.cannotMoveRpCharAlreadyMoving(rpChar).getMessage());

        log.info("Test passed: createRpCharMovement throws ServiceException when Char is already in a movement!");
    }

    @Test
    void ensureCancelRpCharMovementWorks() {
        log.debug("Testing if cancelRpCharMovement works with valid values!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("91").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();
        Path endPath = Path.builder().path(List.of("91", "92")).cost(2).build();
        Movement movement = Movement.builder().isCharMovement(true).player(player).path(endPath).isCurrentlyActive(true).build();

        log.trace("Initializing Dto");
        DiscordIdDto dto = new DiscordIdDto("1234");

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));
        when(mockMovementRepository.findMovementByPlayerAndIsCurrentlyActiveTrue(player)).thenReturn(Optional.of(movement));
        when(mockMovementRepository.save(movement)).thenReturn(movement);

        //Act
        Movement newMovement = movementService.cancelRpCharMovement(dto);

        //Assert
        log.debug("Starting asserts");
        assertThat(newMovement).isEqualTo(movement);
        assertThat(movement.getIsCurrentlyActive()).isFalse();

        log.info("Test passed: cancelRpCharMovement works with valid values!");
    }

    @Test
    void ensureCancelRpCharMovementThrowsIAEWhenNoPlayer() {
        log.debug("Testing if cancelRpCharMovement throws ServiceException when no player is found!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        DiscordIdDto dto = new DiscordIdDto("1234");

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.empty());

        //Act
        var exception = assertThrows(IllegalArgumentException.class, () -> movementService.cancelRpCharMovement(dto));

        //Assert
        log.debug("Starting asserts");
        assertThat(exception.getMessage()).contains("No player found");

        log.info("Test passed: cancelRpCharMovement throws ServiceException when no player is found!");
    }

    @Test
    void ensureCancelRpCharMovementThrowsSEWhenNoRpChar() {
        log.debug("Testing if cancelRpCharMovement throws ServiceException when player has no rp char!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(null).build();
        DiscordIdDto dto = new DiscordIdDto("1234");

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));

        //Act
        var exception = assertThrows(PlayerServiceException.class, () -> movementService.cancelRpCharMovement(dto));

        //Assert
        log.debug("Starting asserts");
        assertThat(exception.getMessage()).isEqualTo(PlayerServiceException.noRpChar().getMessage());

        log.info("Test passed: cancelRpCharMovement throws ServiceException when player has no rp char!");
    }

    @Test
    void ensureCancelRpCharMovementThrowsSEWhenNoMovement() {
        log.debug("Testing if cancelRpCharMovement throws ServiceException when no active movement is found!");

        // Assign
        log.trace("Initializing player, rpchar and regions");
        Region fromRegion = Region.builder().id("91").build();
        RPChar rpChar = RPChar.builder().name("Belegorn Arnorion").currentRegion(fromRegion).build();
        Player player = Player.builder().discordID("1234").ign("Lüktrönic").uuid("huehue").rpChar(rpChar).build();

        log.trace("Initializing Dto");
        DiscordIdDto dto = new DiscordIdDto("1234");

        log.trace("Mocking methods");
        when(mockPlayerRepository.findByDiscordID("1234")).thenReturn(Optional.of(player));
        when(mockMovementRepository.findMovementsByPlayer(player)).thenReturn(List.of());

        //Act
        var exception = assertThrows(ServiceException.class, () -> movementService.cancelRpCharMovement(dto));

        //Assert
        log.debug("Starting asserts");
        assertThat(exception.getMessage()).isEqualTo(MovementServiceException.noActiveMovementChar(rpChar.getName()).getMessage());

        log.info("Test passed: cancelRpCharMovement throws ServiceException when no active movement is found!");
    }

    @Test
    void ensureCreateArmyMovementWorksProperly() {
        log.debug("Testing if createArmyMovement works properly given the correct values");

        MoveArmyDto dto = new MoveArmyDto(player.getDiscordID(), army.getName(), region2.getId());
        army.setBoundTo(player);
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.empty());

        log.debug("Calling createArmyMovement, expecting no errors");
        var result = movementService.createArmyMovement(dto);

        assertThat(result).isNotNull();
        assertThat(result.getArmy()).isEqualTo(army);
        assertThat(result.getPlayer()).isEqualTo(player);
        assertThat(result.getIsAccepted()).isFalse();
        assertThat(result.getIsCurrentlyActive()).isTrue();
        assertThat(result.getIsCharMovement()).isFalse();
        assertThat(result.getPath()).isEqualTo(movement.getPath());

        log.info("Test passed: createArmyMovement works properly with correct values");
    }
    @Test
    void ensureCreateArmyMovementThrowsSeWhenPlayerIsNotRegistered() {
        log.debug("Testing if createArmyMovement throws Se when region does not exist");

        MoveArmyDto dto = new MoveArmyDto(player.getDiscordID(), army.getName(), "S2");

        when(mockRegionRepository.findById(dto.toRegion())).thenReturn(Optional.empty());

        log.debug("Calling createArmyMovement, expecting Se");
        var result = assertThrows(ServiceException.class, () -> movementService.createArmyMovement(dto));

        assertThat(result.getMessage()).contains("Desired destination region 'S2'");
        log.info("Test passed: createArmyMovement throws Se when region does not exist");
    }

    @Test
    void ensureCreateArmyMovementThrowsSeWhenArmyAndDestinationRegionsAreTheSame() {
        log.debug("Testing if createArmyMovement throws Se when destination region and current army region are the same");

        MoveArmyDto dto = new MoveArmyDto(player.getDiscordID(), army.getName(), region1.getId());

        log.debug("Calling createArmyMovement, expecting Se");
        var result = assertThrows(ArmyServiceException.class, () -> movementService.createArmyMovement(dto));

        assertThat(result.getMessage()).contains("is already in the desired region");
        log.info("Test passed: createArmyMovement throws Se when current army region and desired region are the same");
    }

    @Test
    void ensureCreateArmyMovementThrowsSEWhenArmyCreatedLessThan24hAgo() {
        log.debug("Testing if createArmyMovement throws ASE when army was created less than 24h ago!");

        army.setCreatedAt(LocalDateTime.now().minusMinutes(59).minusHours(23));
        log.debug("Now: [{}]", LocalDateTime.now());
        log.debug("Created: [{}]", army.getCreatedAt());


        MoveArmyDto dto = new MoveArmyDto(player.getDiscordID(), army.getName(), region2.getId());
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.empty());

        log.debug("Calling createArmyMovement, expecting Se");
        var result = assertThrows(ArmyServiceException.class, () -> movementService.createArmyMovement(dto));

        assertThat(result.getMessage()).isEqualTo(ArmyServiceException.cannotMoveArmyWasCreatedRecently(army.getName(), 1).getMessage());
        log.info("Test passed: createArmyMovement throws ASE when army was created less than 24h ago!");
    }

    @Test
    void ensureCreateArmyMovementThrowsSeWhenArmyIsAlreadyMoving() {
        log.debug("Testing if createArmyMovement throws Se when specified army is already moving");

        MoveArmyDto dto = new MoveArmyDto(player.getDiscordID(), army.getName(), region2.getId());
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.of(new Movement()));

        log.debug("Calling createArmyMovement, expecting Se");
        var result = assertThrows(ArmyServiceException.class, () -> movementService.createArmyMovement(dto));

        assertThat(result.getMessage()).contains("because it is already in a movement!");
        log.info("Test passed: createArmyMovement throws Se when Army is already in a movement");
    }

    @Test
    void ensureCreateArmyMovementThrowsSeWhenPlayerIsNotALlowedToPerformMovement() {
        log.debug("Testing if createArmyMovement throws Se when player does not have permission to move");

        MoveArmyDto dto = new MoveArmyDto(player.getDiscordID(), army.getName(), region2.getId());
        army.setBoundTo(null);
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.empty());

        log.debug("Calling createArmyMovement, expecting Se");
        var result = assertThrows(ArmyServiceException.class, () -> movementService.createArmyMovement(dto));

        assertThat(result.getMessage()).contains("No permission to perform this action");
        log.info("Test passed: createArmyMovement throws Se when player is not allowed to move");
    }
    @Test
    void ensureCancelArmyMovementWorks() {
        log.debug("Testing if cancelArmyMovement works with proper data!");

        //Assign
        log.trace("Initializing data");
        String armyName = "Knights of Gondor";
        Faction faction = Faction.builder().name("Gondor").build();
        Army army = Army.builder().name(armyName).faction(faction).build();
        Path path = Path.builder().path(List.of("90", "92")).build();
        Movement movement = Movement.builder().isCharMovement(false).army(army).path(path).isCurrentlyActive(true).build();
        RPChar rpchar = RPChar.builder().name("Belegorn").boundTo(army).build();
        Player player = Player.builder().ign("Luktronic").discordID("1234").faction(faction).rpChar(rpchar).build();
        army.setBoundTo(player);
        MoveArmyDto dto = new MoveArmyDto("1234", armyName, null);

        when(mockArmyService.getArmyByName(armyName)).thenReturn(army);
        when(mockPlayerService.getPlayerByDiscordId("1234")).thenReturn(player);
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.of(movement));

        //Act
        log.trace("Calling cancelArmyMovement");
        movementService.cancelArmyMovement(dto);

        //Assert
        log.trace("Asserting");
        assertThat(movement.getIsCurrentlyActive()).isFalse();

        log.info("Test passed: cancelArmyMovement works with proper data!");
    }

    @Test
    void ensureCancelArmyMovementThrowsSEWhenArmyNotFound() {
        log.debug("Testing if cancelArmyMovement throws Service Exception when Army not found!");

        //Assign
        log.trace("Initializing data");
        String armyName = "Knights of Gondor";
        Army army = Army.builder().name(armyName).build();
        MoveArmyDto dto = new MoveArmyDto("1234", armyName, null);

        when(mockArmyService.getArmyByName(armyName)).thenThrow(ArmyServiceException.noArmyWithName("Army or Company", armyName));

        //Act / Assert
        log.trace("Calling cancelArmyMovement and asserting it throws ArmyServiceException");
        var exception = assertThrows(ArmyServiceException.class, () -> movementService.cancelArmyMovement(dto));

        //Assert
        log.trace("Asserting");
        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.noArmyWithName("Army or Company", armyName).getMessage());

        log.info("Test passed: cancelArmyMovement throws Service Exception when Army not found!");
    }



    @Test
    void ensureCancelArmyMovementThrowsSEWhenPlayerNotAllowed() {
        log.debug("Testing if cancelArmyMovement throws MovementServiceException when the player is not faction leader!");

        //Assign
        log.trace("Initializing data");
        String armyName = "Knights of Gondor";
        Faction gondor = Faction.builder().name("Gondor").build();
        Army army = Army.builder().name(armyName).faction(gondor).build();
        RPChar rpchar = RPChar.builder().name("Belegorn").build();
        Player player = Player.builder().faction(gondor).ign("Luktronic").discordID("1234").rpChar(rpchar).build();
        MoveArmyDto dto = new MoveArmyDto("1234", armyName, null);

        when(mockPlayerService.getPlayerByDiscordId("1234")).thenReturn(player);
        when(mockArmyService.getArmyByName(armyName)).thenReturn(army);

        //Act / Assert
        log.trace("Calling cancelArmyMovement and asserting it throws MovementServiceException");
        var exception = assertThrows(MovementServiceException.class, () -> movementService.cancelArmyMovement(dto));

        //Assert
        log.trace("Asserting");
        assertThat(exception.getMessage()).isEqualTo(MovementServiceException.notAllowedToCancelMove().getMessage());

        log.info("Test passed: cancelArmyMovement throws ArmyServiceException when the player is not allowed to move!");
    }

    @Test
    void ensureCancelArmyMovementThrowsSEWhenNoMovement() {
        log.debug("Testing if cancelArmyMovement throws Service Exception when no Movement is found!");

        //Assign
        log.trace("Initializing data");
        String armyName = "Knights of Gondor";
        Faction gondor = Faction.builder().name("Gondor").build();
        Army army = Army.builder().name(armyName).faction(gondor).build();
        RPChar rpchar = RPChar.builder().name("Belegorn").build();
        Player player = Player.builder().faction(gondor).ign("Luktronic").discordID("1234").rpChar(rpchar).build();
        gondor.setLeader(player);
        MoveArmyDto dto = new MoveArmyDto("1234", armyName, null);

        when(mockArmyService.getArmyByName(armyName)).thenReturn(army);
        when(mockPlayerService.getPlayerByDiscordId("1234")).thenReturn(player);
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.empty());

        //Act / Assert
        log.trace("Calling cancelArmyMovement and asserting it throws ArmyServiceException");
        var exception = assertThrows(MovementServiceException.class, () -> movementService.cancelArmyMovement(dto));

        //Assert
        log.trace("Asserting");
        assertThat(exception.getMessage()).isEqualTo(MovementServiceException.noActiveMovementArmy(armyName).getMessage());

        log.info("Test passed: cancelArmyMovement throws Service Exception when no Movement is found!");
    }
}
