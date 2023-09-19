package com.ardaslegends.service;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.Battle;
import com.ardaslegends.domain.war.BattleLocation;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;
import com.ardaslegends.repository.BattleRepository;
import com.ardaslegends.repository.MovementRepository;
import com.ardaslegends.repository.war.WarRepository;
import com.ardaslegends.service.dto.war.CreateBattleDto;
import com.ardaslegends.service.war.BattleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<PathElement> path;

    private Movement movement;

    @BeforeEach
    void setup(){
        mockBattleRepository = mock(BattleRepository.class);
        mockWarRepository = mock(WarRepository.class);
        pathfinder = mock(Pathfinder.class);
        mockArmyService = mock(ArmyService.class);
        mockPlayerService = mock(PlayerService.class);
        mockClaimBuildService = mock(ClaimBuildService.class);
        battleService = new BattleService(mockBattleRepository,mockArmyService,mockPlayerService,mockClaimBuildService,mockWarRepository,pathfinder);

        region1 = Region.builder().id("90").regionType(RegionType.LAND).build();
        region2 = Region.builder().id("91").regionType(RegionType.LAND).build();
        region3 = Region.builder().id("92").regionType(RegionType.LAND).build();

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
        rpchar2 = RPChar.builder().name("Gandalf").isHealing(false).currentRegion(region1).build();

        player1 = Player.builder().discordID("1234").faction(faction1).build();
        player1.addActiveRpChar(rpchar1);
        player2 = Player.builder().discordID("4321").faction(faction2).build();
        player2.addActiveRpChar(rpchar2);

        army1 = Army.builder().name("Knights of Gondor").armyType(ArmyType.ARMY).faction(faction1).freeTokens(30 - unit1.getCount() * unitType1.getTokenCost()).currentRegion(region1).boundTo(player1.getActiveCharacter().get()).stationedAt(claimBuild1).sieges(new ArrayList<>()).createdAt(LocalDateTime.now().minusDays(3)).build();
        army2 = Army.builder().name("Knights of Isengard").armyType(ArmyType.ARMY).faction(faction2).freeTokens(30 - unit2.getCount() * unitType2.getTokenCost()).currentRegion(region2).boundTo(player2.getActiveCharacter().get()).stationedAt(claimBuild2).sieges(new ArrayList<>()).createdAt(LocalDateTime.now().minusDays(3)).build();

        warParticipant1= WarParticipant.builder().warParticipant(faction1).initialParty(true).joiningDate(LocalDateTime.now()).build();
        warParticipant1= WarParticipant.builder().warParticipant(faction2).initialParty(true).joiningDate(LocalDateTime.now()).build();

         Set<WarParticipant> attacker = new HashSet<>();
         Set<WarParticipant> defender = new HashSet<>();

         attacker.add(warParticipant1);
         defender.add(warParticipant2);

        war = War.builder().name("War of Gondor").aggressors(attacker).defenders(defender).startDate(LocalDateTime.now()).build();

        Set<Army> attackingArmies = new HashSet<>();
        attackingArmies.add(army1);
        Set<Army> defendingArmies = new HashSet<>();
        attackingArmies.add(army2);

        battleLocation = new BattleLocation(region2,false,claimBuild2);

        battle = new Battle(war,"Battle of Gondor",attackingArmies,defendingArmies, LocalDateTime.now(),LocalDateTime.of(2023,9,20,0,0),LocalDateTime.of(2023,9,30,0,0),LocalDateTime.of(2023,9,20,0,0),battleLocation);

        pathElement1 = PathElement.builder().region(region1).baseCost(region1.getCost()).actualCost(region1.getCost()).build();
        pathElement2 = PathElement.builder().region(region2).baseCost(region2.getCost()).actualCost(region2.getCost()).build();
        path = List.of(pathElement1, pathElement2);

        movement =  Movement.builder().isCharMovement(false).isCurrentlyActive(true).army(army1).path(path).build();

        when(mockPlayerService.getPlayerByDiscordId(player1.getDiscordID())).thenReturn(player1);
        when(mockPlayerService.getPlayerByDiscordId(player2.getDiscordID())).thenReturn(player2);
        when(mockArmyService.getArmyByName(army1.getName())).thenReturn(army1);
        when(mockArmyService.getArmyByName(army2.getName())).thenReturn(army2);
        when(pathfinder.findShortestWay(any(),any(),any(),anyBoolean())).thenReturn(movement.getPath());
        when(mockClaimBuildService.getClaimBuildByName(claimBuild1.getName())).thenReturn(claimBuild1);
        when(mockClaimBuildService.getClaimBuildByName(claimBuild2.getName())).thenReturn(claimBuild2);
        when(mockClaimBuildService.getClaimBuildByName(claimBuild3.getName())).thenReturn(claimBuild3);
        when(mockWarRepository.isFactionAtWarWithOtherFaction(faction1,faction2)).thenReturn(true);

    }


    @Test
    void ensureCreateBattleWorks(){
        log.debug("Testing if createBattle works with valid values!");

        // Assign
        log.trace("Initializing player, rpchar, regions, army");
        CreateBattleDto createBattleDto = new CreateBattleDto("1234","Battle of Gondor","Knights of Gondor","Knights of Isengard",false,"Aira");

        Battle newBattle = battleService.createBattle(createBattleDto);
        log.debug(newBattle.getName());

    }
}
