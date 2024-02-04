package com.ardaslegends.repository;

import com.ardaslegends.domain.*;
import com.ardaslegends.domain.war.Battle;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.repository.faction.FactionRepository;
import com.ardaslegends.repository.war.QueryWarStatus;
import com.ardaslegends.repository.war.WarRepository;
import io.vavr.collection.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
@ActiveProfiles("test")
public class ArmyRepositoryTest {

    @Autowired
    ArmyRepository armyRepository;
    @Autowired
    FactionRepository factionRepository;

    private Faction gondor;
    private Region region1;
    private RPChar rpChar;
    private ClaimBuild claimbuild;

    @BeforeEach
    void setup() {
        region1 = Region.builder().name("").id("1").regionType(RegionType.DESERT).build();
        val region2 = Region.builder().name("").id("2").regionType(RegionType.DESERT).build();
        val region3 = Region.builder().name("").id("3").regionType(RegionType.DESERT).build();
        val region4 = Region.builder().name("").id("4").regionType(RegionType.DESERT).build();
        val region5 = Region.builder().name("").id("5").regionType(RegionType.DESERT).build();

        gondor = Faction.builder().name("Gondor").homeRegion(region1).build();
        rpChar = RPChar.builder().name("Belegorn").currentRegion(region1).build();
        var mordor = Faction.builder().name("Mordor").homeRegion(region2).build();
        claimbuild = ClaimBuild.builder().name("Nimheria").coordinates(new Coordinate(0,0,0)).ownedBy(gondor)
                .region(region1).type(ClaimBuildType.CASTLE).build();

        factionRepository.saveAll(List.of(gondor, mordor));
    }

    @Test
    void ensureSaveArmyWorks() {
        val army = new Army("Army", ArmyType.ARMY, gondor, region1, rpChar, new ArrayList<>(),
                new ArrayList<>(), null, 0.0, false, null, null, 0, 0, claimbuild,
                OffsetDateTime.now(), true);

        val result = armyRepository.save(army);

        assertThat(result).isNotNull();
    }

}
