package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.UnitTypeService;
import com.ardaslegends.data.service.dto.army.*;
import com.ardaslegends.data.service.dto.unit.UnitTypeDto;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.service.exceptions.claimbuild.ClaimBuildServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ArmyServiceTest {

    private ArmyService armyService;

    private ArmyRepository mockArmyRepository;
    private MovementRepository mockMovementRepository;
    private FactionRepository mockFactionRepository;
    private PlayerService mockPlayerService;
    private UnitTypeService mockUnitTypeService;
    private ClaimBuildRepository mockClaimBuildRepository;

    private BindArmyDto dto;
    private Faction faction;
    private Region region1;
    private Region region2;
    private RPChar rpchar;
    private Player player;
    private Army army;
    private Movement movement;
    private ClaimBuild claimBuild;

    @BeforeEach
    void setup() {
        mockArmyRepository = mock(ArmyRepository.class);
        mockMovementRepository = mock(MovementRepository.class);
        mockFactionRepository = mock(FactionRepository.class);
        mockPlayerService = mock(PlayerService.class);
        mockUnitTypeService = mock(UnitTypeService.class);
        mockClaimBuildRepository = mock(ClaimBuildRepository.class);
        armyService = new ArmyService(mockArmyRepository, mockMovementRepository,mockPlayerService, mockFactionRepository, mockUnitTypeService, mockClaimBuildRepository);

        region1 = Region.builder().id("90").build();
        region2 = Region.builder().id("91").build();
        faction = Faction.builder().name("Gondor").allies(new ArrayList<>()).build();
        claimBuild = ClaimBuild.builder().name("Nimheria").siege("Ram, Trebuchet, Tower").region(region1).ownedBy(faction).specialBuildings(List.of(SpecialBuilding.HOUSE_OF_HEALING)).build();
        rpchar = RPChar.builder().name("Belegorn").currentRegion(region1).build();
        player = Player.builder().discordID("1234").faction(faction).rpChar(rpchar).build();
        army = Army.builder().name("Knights of Gondor").armyType(ArmyType.ARMY).faction(faction).freeTokens(0).currentRegion(region2).stationedAt(claimBuild).sieges(new ArrayList<>()).build();
        movement =  Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army).path(Path.builder().path(List.of("90", "91")).build()).build();

        when(mockPlayerService.getPlayerByDiscordId(player.getDiscordID())).thenReturn(player);
        when(mockArmyRepository.findArmyByName(army.getName())).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.of(movement));
        when(mockClaimBuildRepository.findById(claimBuild.getName())).thenReturn(Optional.of(claimBuild));
    }

    // Create Army
    @Test
    void ensureCreateArmyWorksProperly() {
        log.debug("Testing if createArmy works properly");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 11)});
        ClaimBuild claimBuild = new ClaimBuild();
        ClaimBuildType type = ClaimBuildType.TOWN;
        claimBuild.setType(type);

        Faction faction = Faction.builder().name("Gondr").build();
        claimBuild.setOwnedBy(faction);
        Player player = Player.builder().discordID(dto.executorDiscordId()).faction(faction).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(player);
        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 1.0));
        when(mockClaimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.of(claimBuild));
        when(mockArmyRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        log.debug("Calling createArmy()");
        var result = armyService.createArmy(dto);

        assertThat(result.getFreeTokens()).isEqualTo(30-11);
        log.info("Test passed: CreateArmy works properly with correct values");
    }

    @Test
    void ensureCreateArmyThrowsIAEWhenArmyNameIsAlreadyTaken() {
        log.debug("Testing if createArmy correctly throws IAE when name is already taken");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});

        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.of(new Army()));

        log.debug("Expecting IAE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(IllegalArgumentException.class, () -> armyService.createArmy(dto));

        assertThat(result.getMessage()).contains("already exists");
        log.info("Test passed: IAE when Army Name is taken!");
    }

    @Test
    void ensureCreateArmyThrowsIAEWhenNoClaimBuildWithInputNameHasBeenFound() {
        log.debug("Testing if createArmy correctly throws IAE when no claimBuild could be found");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});

        Faction faction = Faction.builder().name("Gondr").build();
        Player player = Player.builder().discordID(dto.executorDiscordId()).faction(faction).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(player);
        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 1.0));
        when(mockClaimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.empty());

        log.debug("Expecting IAE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(ClaimBuildServiceException.class, () -> armyService.createArmy(dto));

        assertThat(result.getMessage()).isEqualTo(ClaimBuildServiceException.noCbWithName(dto.claimBuildName()).getMessage());
        log.info("Test passed: IAE when no ClaimBuild could be found");
    }

    @Test
    void ensureCreateArmyThrowsSEWhenCbIsFromDifferentFaction() {
        log.debug("Testing if createArmy correctly throws ArmyServiceException when claimBuild is from another faction");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto(player.getDiscordID(), army.getName(), ArmyType.ARMY, claimBuild.getName(), new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});
        Faction otherFaction = Faction.builder().name("Dol Amroth").build();
        claimBuild.setOwnedBy(otherFaction);

        log.debug("Expecting IAE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.createArmy(dto));

        assertThat(result.getMessage()).isEqualTo(ArmyServiceException.cannotCreateArmyFromClaimbuildInDifferentFaction(player.getFaction().getName(), claimBuild.getOwnedBy().getName()).getMessage());
        log.info("Test passed: createArmy throws ArmyServiceException when claimBuild is from another faction");
    }
    @Test
    void ensureCreateArmyThrowsServiceExceptionWhenClaimBuildHasReachedMaxArmies() {
        log.debug("Testing if createArmy correctly throws SE when max armies is already reached");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});
        ClaimBuild claimBuild = new ClaimBuild();
        ClaimBuildType type = ClaimBuildType.HAMLET;
        claimBuild.setType(type);

        Faction faction = Faction.builder().name("Gondr").build();
        claimBuild.setOwnedBy(faction);
        Player player = Player.builder().discordID(dto.executorDiscordId()).faction(faction).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(player);
        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 1.0));
        when(mockClaimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.of(claimBuild));

        log.debug("Expecting SE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.createArmy(dto));

        log.info("Test passed: SE on max armies from ClaimBuild");
    }
    @Test
    void ensureCreateArmyThrowsServiceExceptionWhenUnitsExceedAvailableTokens() {
        log.debug("Testing if createArmy correctly throws SE when units exceed available tokens");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 11)});
        ClaimBuild claimBuild = new ClaimBuild();
        ClaimBuildType type = ClaimBuildType.TOWN;
        claimBuild.setType(type);

        Faction faction = Faction.builder().name("Gondr").build();
        claimBuild.setOwnedBy(faction);
        Player player = Player.builder().discordID(dto.executorDiscordId()).faction(faction).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(player);
        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 3.0));
        when(mockClaimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.of(claimBuild));

        log.debug("Expecting SE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.createArmy(dto));

        log.info("Test passed: SE on exceeding token count");
    }

    // Healing start Tests

    @Test
    void ensureHealStartWorksProperly() {
        log.debug("Testing if heal start works properly with correct values");

        log.trace("Initializing data");

        UpdateArmyDto dto = new UpdateArmyDto(player.getDiscordID(), army.getName(), null);

        log.debug("Expecting no errors");
        log.debug("Calling healStart");
        var result = armyService.healStart(dto);

        assertThat(army.isHealing()).isTrue();
        log.info("Test passed: heal start works properly with correct values");
    }
    @Test
    void ensureHealStartThrwosSeWhenArmyAndPlayerAreNotInTheSameFaction() {
        log.debug("Testing if healStart correctly throws SE when Player and Army are not in the same faction");

        log.trace("Initializing data");
        army.setFaction(Faction.builder().name("Kekw").build());

        UpdateArmyDto dto = new UpdateArmyDto(player.getDiscordID(),army.getName(), null);

        log.debug("Expecting SE on call");
        log.debug("Calling healStart");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.healStart(dto));

        assertThat(result.getMessage()).contains("are not in the same faction");
        log.info("Test passed: SE if not in the same faction");
    }

    @Test
    void ensureHealStartThrowsSeWhenArmyIsNotStationedAtACbWithHouseOfHealing() {
        log.debug("Testing if healStart correctly throws SE when army is not stationed at House of Healing Cb");

        log.trace("Initializing data");
        claimBuild.setSpecialBuildings(List.of());

        UpdateArmyDto dto = new UpdateArmyDto(player.getDiscordID(), army.getName(),null);

        log.debug("Expecting SE on call");
        log.debug("Calling healStart");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.healStart(dto));

        assertThat(result.getMessage()).contains("not stationed at a CB with a House of Healing");
        log.info("Test passed: SE if army is not stationed at House of Healing");
    }

    // Heal stop tests

    @Test
    void ensureHealStopWorksProperly() {
        log.debug("Testing if heal stop works properly with correct values");

        log.trace("Initializing data");
        army.setHealing(true);

        UpdateArmyDto dto = new UpdateArmyDto(player.getDiscordID(), army.getName(), null);

        log.debug("Expecting no errors");
        log.debug("Calling healStart");
        var result = armyService.healStop(dto);

        assertThat(army.isHealing()).isFalse();
        log.info("Test passed: heal stop works properly with correct values");
    }
    @Test
    void ensureHealStopThrowsSeIfArmyIsNotHealing() {
        log.debug("Testing if heal stop correctly throws SE when army is not healing");

        log.trace("Initializing data");
        army.setHealing(false);

        UpdateArmyDto dto = new UpdateArmyDto(player.getDiscordID(),army.getName(), null);

        log.debug("Expecting SE on call");
        log.debug("Calling healStop");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.healStop(dto));

        assertThat(result.getMessage()).contains("is not healing - Can't stop it");
        log.info("Test passed: SE when army is not healing");
    }

    @Test
    void ensureHealStopThrowsSeIfArmyAndPlayerAreNotInTheSameFaction() {
        log.debug("Testing if heal stop correctly throws SE when army is not in same faction as player");

        log.trace("Initializing data");
        army.setHealing(true);
        army.setFaction(Faction.builder().name("Kekw").build());

        UpdateArmyDto dto = new UpdateArmyDto(player.getDiscordID(),army.getName(), null);

        log.debug("Expecting SE on call");
        log.debug("Calling healStop");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.healStop(dto));

        assertThat(result.getMessage()).contains("are not in the same faction");
        log.info("Test passed: SE when army is not in same faction as player");
    }

    // Station Tests

    @Test
    void ensureStationThrowsCbSeWhenClaimbuildWithGivenNameDoesNotExist() {
        log.debug("Testing if station throws CB Se when no claimbuild exists with given name");

        log.trace("Initializing data");
        when(mockClaimBuildRepository.findById(claimBuild.getName())).thenReturn(Optional.empty());

        StationDto dto = new StationDto(player.getDiscordID(),army.getName(),claimBuild.getName());

        log.debug("Expecting SE on call");
        log.debug("Calling station()");
        var result = assertThrows(ClaimBuildServiceException.class, () -> armyService.station(dto));

        assertThat(result.getMessage()).contains("Found no claimbuild with name");
        log.info("Test passed: station throws SE when no cb founjd");
    }

    @Test
    void ensureBindWorksWhenBindingSelf() {
        log.debug("Testing if army binding works properly!");

        //Assign
        log.trace("Initializing data");
        Faction faction = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpChar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player player = Player.builder().ign("Lüktrönic").discordID("1").faction(faction).rpChar(rpChar).build();
        Army army = Army.builder().name("Gondorian Army").currentRegion(region).armyType(ArmyType.ARMY).faction(faction).build();

        BindArmyDto dto = new BindArmyDto("1", "1", "Gondorian Army");

        when(mockPlayerService.getPlayerByDiscordId("1")).thenReturn(player);
        when(mockArmyRepository.findArmyByName("Gondorian Army")).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);

        log.debug("Calling bind()");
        armyService.bind(dto);

        assertThat(army.getBoundTo()).isEqualTo(player);
        log.info("Test passed: army binding works properly!");
    }

    @Test
    void ensureBindWorksWhenBindingOtherPlayer() {
        log.debug("Testing if army binding works properly on others!");

        //Assign
        log.trace("Initializing data");
        Faction faction = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpChar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player executor = Player.builder().ign("Lüktrönic").discordID("1").faction(faction).rpChar(rpChar).build();
        Player target = Player.builder().ign("aned").discordID("2").faction(faction).rpChar(rpChar).build();
        Army army = Army.builder().name("Gondorian Army").currentRegion(region).armyType(ArmyType.ARMY).faction(faction).build();

        faction.setLeader(executor);

        BindArmyDto dto = new BindArmyDto("1", "2", "Gondorian Army");

        when(mockPlayerService.getPlayerByDiscordId("1")).thenReturn(executor);
        when(mockPlayerService.getPlayerByDiscordId(dto.targetDiscordId())).thenReturn(target);
        when(mockArmyRepository.findArmyByName("Gondorian Army")).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);

        log.debug("Calling bind()");
        armyService.bind(dto);

        assertThat(army.getBoundTo()).isEqualTo(target);
        log.info("Test passed: army binding works properly on other players as faction leader!");
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenNormalPlayerTriesToBindOtherPlayers() {
        log.debug("Testing if SE is thrown when normal player tries to bind other players");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Anedhel", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).build();
        Player aned = Player.builder().discordID(dto.targetDiscordId()).faction(gondor).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetArmyHasDifferentFaction() {
        log.debug("Testing if SE is thrown when target army has a different faction to the target player");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction mordor = Faction.builder().name("Mordor").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).build();
        Player aned = Player.builder().discordID(dto.targetDiscordId()).faction(gondor).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(mordor).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when Army is from a different faction");
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetPlayerIsWandererAndExecutorIsNotFactionLeaderOrLord() {
        log.debug("Testing if SE is thrown when target player is wanderer and executor is not leader or lord");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction wanderer = Faction.builder().name("Wanderer").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).build();
        Player mirak = Player.builder().discordID(dto.targetDiscordId()).faction(wanderer).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockPlayerService.getPlayerByDiscordId(dto.targetDiscordId())).thenReturn(mirak);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when target player is wanderer and executor is not leader or lord");
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetArmyIsInADifferentRegion() {
        log.debug("Testing if SE is thrown when target army is in a different region to the player");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Region region1 = Region.builder().id("90").build();
        Region region2 = Region.builder().id("91").build();
        RPChar rpchar = RPChar.builder().name("Belegorn").currentRegion(region1).build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).currentRegion(region2).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when Army is in a different region");
    }

    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetArmyIsBoundToAPlayer() {
        log.debug("Testing if SE is thrown when target army is already bound to a player");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpchar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).rpChar(rpchar).build();
        Player aned = Player.builder().discordID("1235").faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).currentRegion(region).boundTo(aned).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when Army is already bound to another player");
    }

    @Test
    void ensureBindArmyThrowsServiceExceptionWhenArmyIsMoving() {
        log.debug("Testing if SE is thrown when army is currently moving!");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpchar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).currentRegion(region).boundTo(null).build();
        Movement move = Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army).path(Path.builder().path(List.of("90", "91")).build()).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));
        when(mockMovementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army)).thenReturn(Optional.of(move));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when army is currently moving!");
    }

    @Test
    void ensureBindArmyThrowsServiceExceptionWhenCharIsMoving() {
        log.debug("Testing if SE is thrown when character is currently moving!");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpchar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).currentRegion(region).boundTo(null).build();
        Movement move = Movement.builder().isCharMovement(false).isCurrentlyActive(true).player(luk).path(Path.builder().path(List.of("90", "91")).build()).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));
        when(mockMovementRepository.findMovementByPlayerAndIsCurrentlyActiveTrue(luk)).thenReturn(Optional.of(move));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when character is currently moving!");
    }

    @Test
    void ensureUnbindWorks() {
        log.debug("Testing if unbinding players from armies works");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        RPChar rpchar = RPChar.builder().name("Belegorn").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).boundTo(luk).build();

        when(mockArmyRepository.findArmyByName(army.getName())).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);
        when(mockPlayerService.getPlayerByDiscordId(luk.getDiscordID())).thenReturn(luk);

        log.debug("Calling unbind()");
        armyService.unbind(dto);

        assertThat(army.getBoundTo()).isNull();
        log.info("Test passed: unbind() works with proper data!");
    }

    @Test
    void ensureUnbindThrowsSEWhenTargetIsOtherPlayer() {
        log.debug("Testing if unbinding throws ServiceException when target is other player and executor is not leader!");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "mirak", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        RPChar rpchar = RPChar.builder().name("Tinwe").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).build();
        Player mirak = Player.builder().discordID(dto.targetDiscordId()).faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).boundTo(mirak).build();

        when(mockArmyRepository.findArmyByName(army.getName())).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);
        when(mockPlayerService.getPlayerByDiscordId(luk.getDiscordID())).thenReturn(luk);
        when(mockPlayerService.getPlayerByDiscordId(mirak.getDiscordID())).thenReturn(mirak);

        log.debug("Calling unbind()");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.unbind(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.notFactionLeader(gondor.getName()).getMessage());
        log.info("Test passed: unbind() throws ArmyServiceException when target is other player and executor is not leader");
    }

    @Test
    void ensureUnbindWorksWhenTargetIsOtherPlayerAndExecutorIsLeader() {
        log.debug("Testing if unbinding works when target is other player and executor is leader!");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "mirak", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        RPChar rpchar = RPChar.builder().name("Tinwe").build();
        Player luk = Player.builder().ign("Luk").discordID(dto.executorDiscordId()).faction(gondor).build();
        Player mirak = Player.builder().ign("mirak").discordID(dto.targetDiscordId()).faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).boundTo(mirak).build();
        gondor.setLeader(luk);

        when(mockArmyRepository.findArmyByName(army.getName())).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);
        when(mockPlayerService.getPlayerByDiscordId(luk.getDiscordID())).thenReturn(luk);
        when(mockPlayerService.getPlayerByDiscordId(mirak.getDiscordID())).thenReturn(mirak);

        log.debug("Calling unbind()");
        armyService.unbind(dto);

        assertThat(army.getBoundTo()).isNull();
        log.info("Test passed: unbind() works when target is other player and executor is leader");
    }

    @Test
    void ensureUnbindThrowsSEWhenNoPlayerBound() {
        log.debug("Testing if unbinding throws ArmyServiceException when no player is bound to the army!");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Player luk = Player.builder().ign("Luk").discordID(dto.executorDiscordId()).faction(gondor).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).boundTo(null).build();

        when(mockArmyRepository.findArmyByName(army.getName())).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);
        when(mockPlayerService.getPlayerByDiscordId(luk.getDiscordID())).thenReturn(luk);

        log.debug("Calling unbind()");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.unbind(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.noPlayerBoundToArmy(army.getName()).getMessage());
        log.info("Test passed: unbind() throws ArmyServiceException when no player is bound to the army!");
    }

    @Test
    void ensureGetArmyByNameThrowsServiceExceptionWhenArmyNotFound() {
        log.debug("Testing if ASE is thrown when target army does not exist");

        log.trace("Initializing data");
        String armyName = "Knights of Gondor";

        when(mockArmyRepository.findArmyByName(armyName)).thenReturn(Optional.empty());

        log.debug("Calling getArmyByName");
        log.trace("Expecting ArmyServiceException");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.getArmyByName(armyName));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.noArmyWithName(armyName).getMessage());
        log.info("Test passed: getArmyByName() correctly throws ASE when no Army has been found");
    }

    @Test
    void ensureDisbandArmyWorks() {
        log.debug("Testing if disbandArmy works with proper data!");

        faction.setLeader(player);

        log.trace("Initializing data");
        DeleteArmyDto dto = new DeleteArmyDto(player.getDiscordID(), army.getName());

        log.debug("Calling disbandArmy");
        Army returnedArmy = armyService.disband(dto, false);

        log.debug("Asserting that returned/deleted army is same as inputted army");
        assertThat(returnedArmy).isEqualTo(army);
        log.info("Test passed: disbandArmy works with proper data!");

    }

    @Test
    void ensureDisbandArmyThrowsSEWhenPlayerIsOtherFaction() {
        log.debug("Testing if disbandArmy throws ArmyServiceException when player is in other faction than army");

        log.trace("Initializing data");
        Faction otherFaction = Faction.builder().name("Dol Amroth").build();
        player.setFaction(otherFaction);

        DeleteArmyDto dto = new DeleteArmyDto(player.getDiscordID(), army.getName());

        log.debug("Calling disbandArmy");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.disband(dto, false));

        log.debug("Asserting that error has correct message");
        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.notAllowedToDisbandNotSameFaction(army.getName(), army.getFaction().getName()).getMessage());
        log.info("Test passed: disbandArmy throws ArmyServiceException when player is in other faction than army");
    }

    @Test
    void ensureDisbandArmyThrowsSEWhenPlayerIsNotLeader() {
        log.debug("Testing if disbandArmy throws ArmyServiceException when player is not faction leader");

        log.trace("Initializing data");
        DeleteArmyDto dto = new DeleteArmyDto(player.getDiscordID(), army.getName());

        log.debug("Calling disbandArmy");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.disband(dto, false));

        log.debug("Asserting that error has correct message");
        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.notAllowedToDisband().getMessage());
        log.info("Test passed: disbandArmy throws ArmyServiceException when player is not faction leader");
    }

    @Test
    void ensureForcedDisbandArmyWorks() {
        log.debug("Testing if disbandArmy works when forced!");

        log.trace("Initializing data");
        Faction otherFaction = Faction.builder().name("Dol Amroth").build();
        player.setFaction(otherFaction);

        DeleteArmyDto dto = new DeleteArmyDto(player.getDiscordID(), army.getName());

        log.debug("Calling disbandArmy");
        Army returnedArmy = armyService.disband(dto, true);

        log.debug("Asserting that returned/deleted army is same as inputted army");
        assertThat(returnedArmy).isEqualTo(army);
        log.info("Test passed: disbandArmy works when forced!");
    }

    @Test
    void ensureSetArmyTokensWorks() {
        log.debug("Testing if setArmyTokens works with proper data!");

        log.trace("Initializing data");
        UpdateArmyDto dto = new UpdateArmyDto(null, army.getName(), 10);

        log.debug("Calling setArmyTokens");
        Army returnedArmy = armyService.setFreeArmyTokens(dto);

        assertThat(army.getFreeTokens()).isEqualTo(dto.freeTokens());
        assertThat(returnedArmy).isEqualTo(army);
        log.info("Test passed: setArmyTokens works with proper data!");
    }

    @Test
    void ensureSetArmyTokensThrowsSEWhenTokenAbove30() {
        log.debug("Testing if setArmyTokens throws ArmyServiceException when trying to set tokens to value above 30!");

        log.trace("Initializing data");
        UpdateArmyDto dto = new UpdateArmyDto(null, army.getName(), 40);

        log.debug("Calling setArmyTokens");
        var exception = assertThrows(ArmyServiceException.class ,() -> armyService.setFreeArmyTokens(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.tokenAbove30(dto.freeTokens()).getMessage());
        log.info("Test passed: setArmyTokens throws ArmyServiceException when trying to set tokens to value above 30!");
    }

    @Test
    void ensureSetArmyTokensThrowsSEWhenTokenNegative() {
        log.debug("Testing if setArmyTokens throws ArmyServiceException when trying to set tokens to negative value!");

        log.trace("Initializing data");
        UpdateArmyDto dto = new UpdateArmyDto(null, army.getName(), -1);

        log.debug("Calling setArmyTokens");
        var exception = assertThrows(ArmyServiceException.class ,() -> armyService.setFreeArmyTokens(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.tokenNegative(dto.freeTokens()).getMessage());
        log.info("Test passed: setArmyTokens throws ArmyServiceException when trying to set tokens to negative value!");
    }


    @Test
    void ensurePickSiegeWorks() {
        log.debug("Testing if pickSiege works with proper data!");

        log.trace("Initializing data");
        String siege = "trebuchet";
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);
        army.setBoundTo(player);
        army.setCurrentRegion(claimBuild.getRegion());

        log.debug("Calling pickSiege");
        Army returnedArmy = armyService.pickSiege(dto);

        assertThat(returnedArmy).isEqualTo(army);
        assertThat(army.getSieges()).contains(siege);
        log.info("Test passed: pickSiege works with proper data!");
    }

    @Test
    void ensurePickSiegeWorksWhenPlayerIsLeader() {
        log.debug("Testing if pickSiege works when player is leader!");

        log.trace("Initializing data");
        String siege = "trebuchet";
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);
        faction.setLeader(player);
        army.setCurrentRegion(claimBuild.getRegion());

        log.debug("Calling pickSiege");
        Army returnedArmy = armyService.pickSiege(dto);

        assertThat(returnedArmy).isEqualTo(army);
        assertThat(army.getSieges()).contains(siege);
        log.info("Test passed: pickSiege works when player is leader!");
    }

    @Test
    void ensurePickSiegeThrowsSEWhenArmyIsCompany() {
        log.debug("Testing if pickSiege throws ArmyServiceException when army is Trading Company!");

        log.trace("Initializing data");
        String siege = "trebuchet";
        army.setArmyType(ArmyType.TRADING_COMPANY);
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);

        log.debug("Calling pickSiege");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.pickSiege(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.siegeOnlyArmyCanPick(army.getName()).getMessage());
        log.info("Test passed: pickSiege throws ArmyServiceException when army is Trading Company!");
    }

    @Test
    void ensurePickSiegeThrowsSEWhenPlayerIsNotBoundOrLeader() {
        log.debug("Testing if pickSiege throws ArmyServiceException when player is not bound or leader!");

        log.trace("Initializing data");
        String siege = "trebuchet";
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);
        army.setCurrentRegion(claimBuild.getRegion());

        log.debug("Calling pickSiege");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.pickSiege(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.siegeNotFactionLeaderOrLord(faction.getName(), army.getName()).getMessage());
        log.info("Test passed: pickSiege throws ArmyServiceException when player is not bound or leader!");
    }

    @Test
    void ensurePickSiegeThrowsSEWhenCbNotFound() {
        log.debug("Testing if pickSiege throws ClaimBuildServiceException when no CB with name is found!");

        log.trace("Initializing data");
        String siege = "trebuchet";
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);
        faction.setLeader(player);
        army.setCurrentRegion(claimBuild.getRegion());

        when(mockClaimBuildRepository.findById(claimBuild.getName())).thenReturn(Optional.empty());

        log.debug("Calling pickSiege");
        var exception = assertThrows(ClaimBuildServiceException.class, () -> armyService.pickSiege(dto));

        assertThat(exception.getMessage()).isEqualTo(ClaimBuildServiceException.noCbWithName(claimBuild.getName()).getMessage());
        log.info("Test passed: pickSiege throws ClaimBuildServiceException when no CB with name is found!");
    }

    @Test
    void ensurePickSiegeThrowsSEWhenArmyNotInSameRegionAsCb() {
        log.debug("Testing if pickSiege throws ArmyServiceException when army is in different region than cb!");

        log.trace("Initializing data");
        String siege = "trebuchet";
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);
        faction.setLeader(player);

        log.debug("Calling pickSiege");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.pickSiege(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.siegeArmyNotInSameRegionAsCB(army.getName(), army.getCurrentRegion().getId(),
                claimBuild.getName(), claimBuild.getRegion().getId()).getMessage());
        log.info("Test passed: pickSiege throws ArmyServiceException when army is in different region than cb!");
    }

    @Test
    void ensurePickSiegeThrowsSEWhenCbNotSameFactionOrAllied() {
        log.debug("Testing if pickSiege throws ClaimBuildServiceException when CB isn't from same faction or allied faction!");

        log.trace("Initializing data");
        String siege = "trebuchet";
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);
        Faction faction2 = Faction.builder().name("Dol Amroth").build();
        faction.setLeader(player);
        army.setCurrentRegion(claimBuild.getRegion());
        claimBuild.setOwnedBy(faction2);

        log.debug("Calling pickSiege");
        var exception = assertThrows(ClaimBuildServiceException.class, () -> armyService.pickSiege(dto));

        assertThat(exception.getMessage()).isEqualTo(ClaimBuildServiceException.differentFactionNotAllied(claimBuild.getName(), claimBuild.getOwnedBy().getName()).getMessage());
        log.info("Test passed: pickSiege throws ClaimBuildServiceException when CB isn't from same faction or allied faction!");
    }

    @Test
    void ensurePickSiegeThrowsSEWhenSiegeNotAvailableInCb() {
        log.debug("Testing if pickSiege throws ArmyServiceException when siege is not available in cb!");

        log.trace("Initializing data");
        String siege = "hueueheuheu";
        PickSiegeDto dto = new PickSiegeDto(player.getDiscordID(),army.getName(), claimBuild.getName(), siege);
        faction.setLeader(player);
        army.setCurrentRegion(claimBuild.getRegion());

        log.debug("Calling pickSiege");
        var exception = assertThrows(ArmyServiceException.class, () -> armyService.pickSiege(dto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.siegeNotAvailable(siege, claimBuild.getName(), claimBuild.getSiege()).getMessage());
        log.info("Test passed: pickSiege throws ArmyServiceException when siege is not available in cb!");
    }
}
