package com.ardaslegends.service;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.ArmyType;
import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.domain.RegionType;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.Pathfinder;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PathfinderTest {

    @Autowired
    private RegionRepository repository;
    private Pathfinder pathfinder;
    private Player player;
    private Faction faction_good;

    @BeforeAll
    void setup() {
        // Initialize Data
        Region r1 = new Region("1", "one", RegionType.LAND, null, null, null);
        Region r2 = new Region("2", "two", RegionType.MOUNTAIN, null, null, null);
        Region r3 = new Region("3", "three", RegionType.LAND, null, null, null);
        Region r4 = new Region("4", "four", RegionType.HILL, null, null, null);
        Region r5 = new Region("5", "five", RegionType.LAND, null, null, null);
        Region r6 = new Region("6", "six", RegionType.LAND, null, null, null);
        Region rs1 = new Region("1.S", "one_sea", RegionType.SEA, null, null, null);
        Region rs2 = new Region("2.S", "two_sea", RegionType.SEA, null, null, null);

        player = new Player("VernonRoche", "VernonRocheDiscord", null, null);
        faction_good = new Faction("Gondor", player, new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), "white", r1, "Double move in Gondor");
        Faction faction_bad = new Faction("Mordor", null, new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), "black", r3, "Move in Mordor");

        Army army = new Army("Test army", ArmyType.ARMY, faction_good, r1, null, new ArrayList<>(), new ArrayList<>(), null, 15, null);
        RPChar rpchar = new RPChar("Aldwin", player, r1, army);

        // Set up relations
        r1.getClaimedBy().add(faction_good);
        r3.getClaimedBy().add(faction_bad);

        player.setFaction(faction_good);
        player.setRpChar(rpchar);

        faction_good.getArmies().add(army);
        faction_good.getPlayers().add(player);
        faction_good.getRegions().add(r1);

        army.setBoundTo(rpchar);

        // Set up region neighbours
        r1.addNeighbour(r2);
        r1.addNeighbour(r3);
        r1.addNeighbour(r4);
        r2.addNeighbour(r1);
        r2.addNeighbour(r3);
        r2.addNeighbour(rs1);
        r3.addNeighbour(r1);
        r3.addNeighbour(r2);
        r3.addNeighbour(r5);
        r3.addNeighbour(r6);
        r3.addNeighbour(rs1);
        r3.addNeighbour(rs2);
        r4.addNeighbour(r1);
        r4.addNeighbour(r3);
        r5.addNeighbour(r3);
        r5.addNeighbour(r4);
        r5.addNeighbour(r6);
        r6.addNeighbour(r3);
        r6.addNeighbour(r5);
        r6.addNeighbour(rs2);
        rs1.addNeighbour(r2);
        rs1.addNeighbour(r3);
        rs1.addNeighbour(rs2);
        rs2.addNeighbour(r3);
        rs2.addNeighbour(r6);
        rs2.addNeighbour(rs1);

        repository.saveAll(List.of(r1,r2,r3,r4,r5,r6,rs1,rs2));
        pathfinder = Pathfinder.getInstance(repository);
    }

    @Test
    void ensureNormalMoveSucceeds(){
        pathfinder.findShortestWay("1", "2", player, false);
    }

    @Test
    void ensureEmbarkingSucceeds(){
        
    }

    @Test
    void ensureThatMoveInEnemyFails(){
        
    }

    @Test
    void ensureMoveThroughSeaWorks(){
        
    }
}
