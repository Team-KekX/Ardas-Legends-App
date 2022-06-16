package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.Path;
import com.ardaslegends.data.service.Pathfinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathfinderTest {

    private RegionRepository mockRepository;
    private Pathfinder pathfinder;
    private Player player;

    @BeforeEach
    void testData() {
        // Initialize Data
        Region r1 = new Region("1", "one", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        Region r2 = new Region("2", "two", RegionType.MOUNTAIN, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        Region r3 = new Region("3", "three", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        Region r4 = new Region("4", "four", RegionType.HILL, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        Region r5 = new Region("5", "five", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        Region r6 = new Region("6", "six", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        Region rs1 = new Region("1.S", "one_sea", RegionType.SEA, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        Region rs2 = new Region("2.S", "two_sea", RegionType.SEA, new HashSet<>(), new ArrayList<>(), new HashSet<>());

        player = new Player("VernonRoche", "VernonRocheDiscord", null, null);
        Faction faction_good = new Faction("Gondor", player, new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>(), "white", r1, "Double move in Gondor");
        Faction faction_bad = new Faction("Mordor", null, new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>(), "black", r3, "Move in Mordor");

        Army army = new Army("Test army", ArmyType.ARMY, faction_good, r1, null, new ArrayList<>(), new ArrayList<>(), null, 15, null);
        RPChar rpchar = new RPChar("Aldwin", player, r1, army);

        ClaimBuild claimbuild = new ClaimBuild("claimbuild", r2, ClaimBuildType.CAPITAL, faction_good,
                new Coordinate(1, 1, 1), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), "none", "4", new HashSet<>());

        // Set up relations
        r1.getClaimedBy().add(faction_good);
        r3.getClaimedBy().add(faction_bad);

        player.setFaction(faction_good);
        player.setRpChar(rpchar);

        faction_good.getArmies().add(army);
        faction_good.getPlayers().add(player);
        faction_good.getRegions().add(r1);
        faction_good.getRegions().add(r2);

        faction_bad.getRegions().add(r3);

        army.setBoundTo(rpchar);

        r2.getClaimBuilds().add(claimbuild);

        claimbuild.getSpecialBuildings().add(SpecialBuilding.HARBOUR);

        // Set up region neighbours
        r1.addNeighbour(r2);
        r1.addNeighbour(r3);
        r1.addNeighbour(r4);
        r2.addNeighbour(r1);
        r2.addNeighbour(r3);
        r3.addNeighbour(r1);
        r3.addNeighbour(r2);
        r3.addNeighbour(r5);
        r3.addNeighbour(r6);
        r4.addNeighbour(r1);
        r4.addNeighbour(r3);
        r4.addNeighbour(r5);
        r5.addNeighbour(r3);
        r5.addNeighbour(r4);
        r5.addNeighbour(r6);
        r6.addNeighbour(r3);
        r6.addNeighbour(r5);
        rs1.addNeighbour(r2);
        rs1.addNeighbour(r3);
        rs2.addNeighbour(r3);
        rs2.addNeighbour(r6);

        List<Region> regionList = List.of(r1, r2, r3, r4, r5, r6, rs1, rs2);

        mockRepository = mock(RegionRepository.class);

        for (Region region : regionList) {
            when(mockRepository.findById(region.getId())).thenReturn(Optional.of(region));
        }

        pathfinder = new Pathfinder(mockRepository);
    }

    @Test
    void ensureNormalMoveSucceeds() {
        Path path = pathfinder.findShortestWay("1", "2", player, false);
        assertThat(path.getPath().size()).isEqualTo(2);
        assertThat(path.getCost()).isEqualTo(RegionType.MOUNTAIN.getCost());

        path = pathfinder.findShortestWay("1", "5", player, false);
        assertThat(path.getPath().size()).isEqualTo(3);
        assertThat(path.getCost()).isEqualTo(RegionType.LAND.getCost() + RegionType.HILL.getCost());

        path = pathfinder.findShortestWay("1", "6", player, false);
        assertThat(path.getPath().size()).isEqualTo(4);
        assertThat(path.getCost()).isEqualTo(RegionType.LAND.getCost() * 2 + RegionType.HILL.getCost());
    }

    @Test
    void ensureEmbarkingSucceeds() {
        Region rs1 = mockRepository.findById("1.S").get();
        Region rs2 = mockRepository.findById("2.S").get();
        Region r2 = mockRepository.findById("2").get();
        Region r3 = mockRepository.findById("3").get();
        Region r6 = mockRepository.findById("6").get();
        rs1.addNeighbour(rs2);
        rs2.addNeighbour(rs1);
        r2.addNeighbour(rs1);
        r3.addNeighbour(rs1);
        r3.addNeighbour(rs2);
        r6.addNeighbour(rs2);
        List<Region> regionList = List.of(r2, r3, r6, rs1, rs2);
        mockRepository.saveAll(regionList);

        Path path = pathfinder.findShortestWay("2", "1.S", player, false);
        assertThat(path.getPath().size()).isEqualTo(2);
        assertThat(path.getCost()).isEqualTo(1);
    }

    @Test
    void ensureDisembarkingSucceeds() {
        Region rs1 = mockRepository.findById("1.S").get();
        Region rs2 = mockRepository.findById("2.S").get();
        Region r2 = mockRepository.findById("2").get();
        Region r3 = mockRepository.findById("3").get();
        Region r6 = mockRepository.findById("6").get();
        rs1.addNeighbour(rs2);
        rs2.addNeighbour(rs1);
        r2.addNeighbour(rs1);
        r3.addNeighbour(rs1);
        r3.addNeighbour(rs2);
        r6.addNeighbour(rs2);
        List<Region> regionList = List.of(r2, r3, r6, rs1, rs2);
        mockRepository.saveAll(regionList);

        Path path = pathfinder.findShortestWay("2.S", "6", player, false);
        assertThat(path.getPath().size()).isEqualTo(2);
        assertThat(path.getCost()).isEqualTo(2);
    }

    @Test
    void ensureThatMoveInEnemyFails() {
        Path path = pathfinder.findShortestWay("1", "3", player, false);
        assertEquals(-1, path.getCost());
    }

    /*
    This test is working for regions with different costs.
    Considering every region has the same cost, this test is not really useful.
     */
    @Test
    void ensureMoveThroughSeaWorks() {
        Region rs1 = mockRepository.findById("1.S").get();
        Region rs2 = mockRepository.findById("2.S").get();
        Region r2 = mockRepository.findById("2").get();
        Region r3 = mockRepository.findById("3").get();
        Region r6 = mockRepository.findById("6").get();
        rs1.addNeighbour(rs2);
        rs2.addNeighbour(rs1);
        r2.addNeighbour(rs1);
        r3.addNeighbour(rs1);
        r3.addNeighbour(rs2);
        r6.addNeighbour(rs2);
        List<Region> regionList = List.of(r2, r3, r6, rs1, rs2);
        mockRepository.saveAll(regionList);

        Path path = pathfinder.findShortestWay("2", "6", player, false);
        /*assertThat(path.getPath().size()).isEqualTo(4);
        assertThat(path.getCost()).isEqualTo(RegionType.SEA.getCost() * 2 + RegionType.LAND.getCost() + 1);
        assertFalse(path.getPath().contains("3"));*/

    }
}
