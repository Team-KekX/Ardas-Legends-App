package com.ardaslegends.repository;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Region;
import com.ardaslegends.domain.RegionType;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;
import com.ardaslegends.repository.faction.FactionRepository;
import com.ardaslegends.repository.war.WarRepository;
import com.ardaslegends.repository.war.WarStatus;
import io.vavr.collection.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
@ActiveProfiles("test")
public class WarRepositoryTest {

    @Autowired
    WarRepository warRepository;
    @Autowired
    FactionRepository factionRepository;

    @BeforeEach
    void setup() {
        val region = Region.builder().name("").id("1").regionType(RegionType.DESERT).build();
        val region2 = Region.builder().name("").id("2").regionType(RegionType.DESERT).build();
        val region3 = Region.builder().name("").id("3").regionType(RegionType.DESERT).build();
        val region4 = Region.builder().name("").id("4").regionType(RegionType.DESERT).build();
        val region5 = Region.builder().name("").id("5").regionType(RegionType.DESERT).build();

        var gondor = Faction.builder().name("Gondor").homeRegion(region).build();
        var mordor = Faction.builder().name("Mordor").homeRegion(region2).build();
        var arnor = Faction.builder().name("Arnor").homeRegion(region3).build();
        var rivendell = Faction.builder().name("Rivendell").homeRegion(region4).build();
        var umbar = Faction.builder().name("Umbar").homeRegion(region5).build();

        /*
        Gondor = 3 (2 active, 1 inactive)
        Mordor = 4 (2 active, 2 inactive)
         */

        War war1 = new War("Minas Ithil", gondor, mordor);
        war1.addToAggressors(arnor);
        war1.addToDefenders(umbar);

        var war2 = new War("Something else", mordor, rivendell);
        war2.addToDefenders(arnor);
        war2.addToAggressors(umbar);

        war2.end();

        var war3 = new War("Another thing", mordor, arnor);
        war3.addToDefenders(gondor);

        var war4 = new War("Keke", mordor, gondor);
        war4.end();

        factionRepository.saveAll(List.of(gondor, mordor, arnor, rivendell, umbar));
        warRepository.saveAll(List.of(war1, war2, war3, war4));
    }

    @Test
    void ensureQueryWarsByFactionWorksProperlyWithOnlyActiveWars() {
        var result = warRepository.queryWarsByFaction(Faction.builder().name("Gondor").build(), WarStatus.ALL_ACTIVE);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.stream().map(War::getName)).containsOnly("Minas Ithil", "Another thing");
    }

    @Test
    void ensureQueryWarsByFactionWorksProperlyActiveAndInactiveWars() {
        var result = warRepository.queryWarsByFaction(Faction.builder().name("Gondor").build(), WarStatus.BOTH);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.stream().map(War::getName)).containsOnly("Minas Ithil", "Another thing", "Keke");
    }

    @Test
    void ensureQueryWarsByFactionWorksProperlWithInactiveWars() {
        var result = warRepository.queryWarsByFaction(Faction.builder().name("Gondor").build(), WarStatus.ALL_INACTIVE);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.stream().map(War::getName)).containsOnly("Keke");
    }


    @Test
    void ensureQueryActiveInitialWarBetweenWorksProperlyWhenNoWarIsFound() {
        val f1 = Faction.builder().name("Umbar").build();
        val f2 = Faction.builder().name("Mordor").build();

        var result = warRepository.queryActiveInitialWarBetween(f1, f2);

        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void ensureQueryActiveInitialWarBetweenWorksProperlyWhenAWarIsFound() {
        var result = warRepository.queryActiveInitialWarBetween(Faction.builder().name("Gondor").build(), Faction.builder().name("Mordor").build());

        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
        val war = result.get();
        assertThat(war.getName()).isEqualTo("Minas Ithil");
    }
}
