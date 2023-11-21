package com.ardaslegends.service;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.Battle;
import com.ardaslegends.domain.war.BattleLocation;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;
import com.ardaslegends.repository.BattleRepository;
import com.ardaslegends.repository.MovementRepository;
import com.ardaslegends.repository.war.WarRepository;
import com.ardaslegends.service.dto.army.MoveArmyDto;
import com.ardaslegends.service.dto.war.CreateBattleDto;
import com.ardaslegends.service.exceptions.logic.army.ArmyServiceException;
import com.ardaslegends.service.exceptions.logic.war.BattleServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import com.ardaslegends.service.war.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class BattleServiceTest {
    private BattleRepository mockBattleRepository;
    private ArmyService mockArmyService;
    private PlayerService mockPlayerService;
    private ClaimBuildService mockClaimBuildService;
    private WarRepository mockWarRepository;
    private Pathfinder pathfinder;
    private BattleService battleService;

    private Faction faction1;
    private Faction faction2;
    private Region region1;
    private Region region2;
    private Region region3;

    private RPChar rpchar1;
    private RPChar rpchar2;

    private Player player1;
    private Player player2;

    private UnitType unitType1;
    private UnitType unitType2;

    private Unit unit1;
    private Unit unit2;

    private Army army1;
    private Army army2;

    private ClaimBuild claimBuild1;
    private ClaimBuild claimBuild2;
    private ClaimBuild claimBuild3;

    private War war;
    private WarParticipant warParticipant1;
    private  WarParticipant warParticipant2;

    private Battle battle;

    private BattleLocation battleLocation;
    private PathElement pathElement1;
    private PathElement pathElement2;
    private PathElement pathElement3;
    private List<PathElement> path;

    private Movement movement;

    Set<Army> attackingArmies;
    Set<Army> defendingArmies;

    CreateBattleDto createBattleDto;
    @BeforeEach
    void setup(){
        mockBattleRepository = mock(BattleRepository.class);
        mockWarRepository = mock(WarRepository.class);
        pathfinder = mock(Pathfinder.class);
        mockArmyService = mock(ArmyService.class);
        mockPlayerService = mock(PlayerService.class);
        mockClaimBuildService = mock(ClaimBuildService.class);
        battleService = new BattleService(mockBattleRepository,mockArmyService,mockPlayerService,mockClaimBuildService,mockWarRepository,pathfinder);

        region1 = Region.builder().id("90").neighboringRegions(new HashSet<>()).regionType(RegionType.LAND).build();
        region2 = Region.builder().id("91").neighboringRegions(new HashSet<>()).regionType(RegionType.LAND).build();
        region3 = Region.builder().id("92").neighboringRegions(new HashSet<>()).regionType(RegionType.LAND).build();

        region1.addNeighbour(region2);
        region2.addNeighbour(region1);
        region2.addNeighbour(region3);
        region3.addNeighbour(region2);

        faction1 = Faction.builder().name("Gondor").allies(new ArrayList<>()).foodStockpile(10).build();
        faction2 = Faction.builder().name("Isengard").allies(new ArrayList<>()).foodStockpile(10).build();

        claimBuild1 = ClaimBuild.builder().name("Nimheria").siege("Ram, Trebuchet, Tower").region(region1).ownedBy(faction1).specialBuildings(List.of(SpecialBuilding.HOUSE_OF_HEALING)).stationedArmies(List.of()).build();
        claimBuild2 = ClaimBuild.builder().name("Aira").siege("Ram, Trebuchet, Tower").region(region2).ownedBy(faction2).specialBuildings(List.of(SpecialBuilding.HOUSE_OF_HEALING)).stationedArmies(List.of()).build();
        claimBuild3 = ClaimBuild.builder().name("Dondle").siege("Ram, Trebuchet, Tower").region(region3).ownedBy(faction2).specialBuildings(List.of(SpecialBuilding.HOUSE_OF_HEALING)).stationedArmies(List.of()).build();

        unitType1 =UnitType.builder().unitName("Gondor Archer").tokenCost(1.5).build();
        unitType2 =UnitType.builder().unitName("Isengard Archer").tokenCost(1.5).build();

        unit1 = Unit.builder().unitType(unitType1).army(army1).amountAlive(5).count(10).build();
        unit2 = Unit.builder().unitType(unitType2).army(army2).amountAlive(5).count(10).build();

        rpchar1 = RPChar.builder().name("Belegorn").isHealing(false).currentRegion(region1).build();
        rpchar1.setId(1L);
        rpchar2 = RPChar.builder().name("Gandalf").isHealing(false).currentRegion(region2).build();
        rpchar2.setId(2L);

        player1 = Player.builder().discordID("1234").ign("Luk").faction(faction1).build();
        player1.addActiveRpChar(rpchar1);
        player2 = Player.builder().discordID("4321").ign("mirak").faction(faction2).build();
        player2.addActiveRpChar(rpchar2);

        army1 = Army.builder().name("Knights of Gondor").armyType(ArmyType.ARMY).faction(faction1).freeTokens(30 - unit1.getCount() * unitType1.getTokenCost()).currentRegion(region1).boundTo(player1.getActiveCharacter().get()).stationedAt(claimBuild1).sieges(new ArrayList<>()).createdAt(LocalDateTime.now().minusDays(3)).build();
        army2 = Army.builder().name("Knights of Isengard").armyType(ArmyType.ARMY).faction(faction2).freeTokens(30 - unit2.getCount() * unitType2.getTokenCost()).currentRegion(region2).boundTo(player2.getActiveCharacter().get()).stationedAt(claimBuild2).sieges(new ArrayList<>()).createdAt(LocalDateTime.now().minusDays(3)).build();

        army2.setMovements(new ArrayList<>());
        army1.setMovements(new ArrayList<>());
        warParticipant1= WarParticipant.builder().warParticipant(faction1).initialParty(true).joiningDate(LocalDateTime.now()).build();
        warParticipant1= WarParticipant.builder().warParticipant(faction2).initialParty(true).joiningDate(LocalDateTime.now()).build();

        Set<WarParticipant> attacker = new HashSet<>();
        Set<WarParticipant> defender = new HashSet<>();

        attacker.add(warParticipant1);
        defender.add(warParticipant2);

        war = War.builder().name("War of Gondor").aggressors(attacker).defenders(defender).startDate(LocalDateTime.now()).build();

        attackingArmies = new HashSet<>();
        attackingArmies.add(army1);
        defendingArmies = new HashSet<>();
        defendingArmies.add(army2);

        battleLocation = new BattleLocation(region2,false,claimBuild2);

        battle = new Battle(war,"Battle of Gondor",attackingArmies,defendingArmies, LocalDateTime.now(),LocalDateTime.of(2023,9,20,0,0),LocalDateTime.of(2023,9,30,0,0),LocalDateTime.of(2023,9,20,0,0),battleLocation);

        pathElement1 = PathElement.builder().region(region1).baseCost(region1.getCost()).actualCost(0).build();
        pathElement2 = PathElement.builder().region(region2).baseCost(region2.getCost()).actualCost(region2.getCost()).build();
        pathElement3 = PathElement.builder().region(region3).baseCost(region3.getCost()).actualCost(region3.getCost()).build();
        path = List.of(pathElement1, pathElement2);

        movement =  Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army1).path(path).build();

        createBattleDto = new CreateBattleDto(player1.getDiscordID(),"Battle of Gondor",
                "Knights of Isengard","Knights of Gondor",true,"Aira");

        when(mockPlayerService.getPlayerByDiscordId(any())).thenReturn(player1);
        when(mockArmyService.getArmyByName(any())).thenReturn(army1);
        when(mockPlayerService.getPlayerByDiscordId(player1.getDiscordID())).thenReturn(player1);
        when(pathfinder.findShortestWay(army1.getCurrentRegion(),region2,player1,false)).thenReturn(movement.getPath());
        when(mockClaimBuildService.getClaimBuildByName(any())).thenReturn(claimBuild1);
        when(mockClaimBuildService.getClaimBuildByName(any())).thenReturn(claimBuild2);
        when(mockClaimBuildService.getClaimBuildByName(any())).thenReturn(claimBuild3);
        when(mockWarRepository.isFactionAtWarWithOtherFaction(any(),any())).thenReturn(true);
        when(mockWarRepository.findWarByAggressorsAndDefenders(any(),any())).thenReturn(war);
        when(mockBattleRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(mockArmyService.getArmyByName("Knights of Gondor")).thenReturn(army1);
        when(mockArmyService.getArmyByName("Knights of Isengard")).thenReturn(army2);

    }

    @Test
    void ensureCreateBattleWorksWhenPlayerBoundToArmy(){
        log.debug("Testing if createBattle works when player is not leader but bound to the army!");

        // Assign
        log.trace("Initializing player, rpchar, regions, army");
        CreateBattleDto createBattleDto = new CreateBattleDto("1234","Battle of Gondor","Knights of Gondor","Knights of Isengard",true,"Aira");

        Battle newBattle = battleService.createBattle(createBattleDto);
        log.debug(newBattle.getName());
        assertThat(newBattle).isNotNull();
        assertThat(newBattle.getName()).isEqualTo("Battle of Gondor");
        assertThat(newBattle.getWar()).isEqualTo(war);
        assertThat(newBattle.getAttackingArmies()).isEqualTo(attackingArmies);
        assertThat(newBattle.getDefendingArmies()).isEqualTo(defendingArmies);
    }

    @Test
    void ensureCreateBattleWorksWhenPlayerIsLeader(){
        log.debug("Testing if createBattle works when player is the faction leader but not bound to the army!");
        army1.setBoundTo(rpchar2);
        faction1.setLeader(player1);

        // Assign
        log.trace("Initializing player, rpchar, regions, army");
        CreateBattleDto createBattleDto = new CreateBattleDto("1234","Battle of Gondor","Knights of Gondor","Knights of Isengard",true,"Aira");

        Battle newBattle = battleService.createBattle(createBattleDto);
        log.debug(newBattle.getName());
        assertThat(newBattle).isNotNull();
        assertThat(newBattle.getName()).isEqualTo("Battle of Gondor");
        assertThat(newBattle.getWar()).isEqualTo(war);
        assertThat(newBattle.getAttackingArmies()).isEqualTo(attackingArmies);
        assertThat(newBattle.getDefendingArmies()).isEqualTo(defendingArmies);
    }

    @Test
    void ensureCreateBattleWorksWhenDefendingArmyIsMovingButCanBeCaught(){
        log.debug("Testing if createBattle works when defending army is moving but can be caught!");

        army2.setCurrentRegion(region1);
        var movementPath = List.of(
                new PathElement(0, army2.getCurrentRegion().getCost(), army2.getCurrentRegion()),
                pathElement2,
                pathElement3);
;
        var movement = new Movement(null, army2, false, movementPath, LocalDateTime.now(),
                LocalDateTime.now().plusDays(ServiceUtils.getTotalPathCost(movementPath)), true, ServiceUtils.getTotalPathCost(movementPath),
                movementPath.get(1).getActualCost(), 0);
        army2.getMovements().add(movement);

        // Assign
        log.trace("Initializing player, rpchar, regions, army");
        CreateBattleDto createBattleDto = new CreateBattleDto("1234","Battle of Gondor","Knights of Gondor","Knights of Isengard",true,"Aira");

        Battle newBattle = battleService.createBattle(createBattleDto);
        log.debug(newBattle.getName());
        assertThat(newBattle).isNotNull();
        assertThat(newBattle.getName()).isEqualTo("Battle of Gondor");
        assertThat(newBattle.getWar()).isEqualTo(war);
        assertThat(newBattle.getAttackingArmies()).isEqualTo(attackingArmies);
        assertThat(newBattle.getDefendingArmies()).isEqualTo(defendingArmies);
    }

    @Test
    void ensureCreateBattleThrowsExceptionWhenPlayerNotBound(){
        //Player1 is not bound to attacking army, so they have no permission to create a battle
        CreateBattleDto createBattleDto = new CreateBattleDto(player1.getDiscordID(),"Battle of Gondor",
                "Knights of Isengard","Knights of Gondor",true,"Aira");

        var exception = assertThrows(ArmyServiceException.class, ()-> battleService.createBattle(createBattleDto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.noPermissionToPerformThisAction().getMessage());
    }

    @Test
    void ensureCreateBattleThrowsNotEnoughHealthException(){
        army1.setFreeTokens(0.0);

        CreateBattleDto createBattleDto = new CreateBattleDto("1234","Battle of Gondor","Knights of Gondor","Knights of Isengard",true,"Aira");

        var exception = assertThrows(BattleServiceException.class, ()-> battleService.createBattle(createBattleDto));

        assertThat(exception.getMessage()).contains("Army does not have enough health");
    }

    @Test
    void ensureCreateBattleThrowsExceptionWhenDefendingArmyIsMovingAway(){
        log.debug("Testing if createBattle works when defending army is moving away and cannot be caught!");

        var movementPath = List.of(
                new PathElement(0, army2.getCurrentRegion().getCost(), army2.getCurrentRegion()),
                new PathElement(region3.getCost(), region3.getCost(), region3),pathElement3);
        ;
        var movement = new Movement(null, army2, false, movementPath, LocalDateTime.now(),
                LocalDateTime.now().plusDays(ServiceUtils.getTotalPathCost(movementPath)), true, ServiceUtils.getTotalPathCost(movementPath),
                movementPath.get(1).getActualCost(), 0);
        army2.getMovements().add(movement);

        var exception = assertThrows(ArmyServiceException.class, ()-> battleService.createBattle(createBattleDto));

        assertThat(exception.getMessage()).isEqualTo(ArmyServiceException.noPermissionToPerformThisAction().getMessage());
    }

}
