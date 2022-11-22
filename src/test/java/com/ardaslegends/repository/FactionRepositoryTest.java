package com.ardaslegends.repository;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.InitialFaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class FactionRepositoryTest {

    @Autowired
    FactionRepository factionRepository;

    @BeforeEach
    void setup() {

        Faction faction = Faction.builder()
                .name("Kek")
                .initialFaction(InitialFaction.ANGMAR)
                .foodStockpile(0)
                .colorcode("KEK")
                .factionRoleId(1929L)
                .build();

        factionRepository.save(faction);

    }

    @Test
    void ensureFindByRoleIdWorksProperly() {

        var result = factionRepository.findFactionByFactionRoleId(1929L);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo("Kek");
        assertThat(result.get().getFactionRoleId()).isEqualTo(1929L);

    }

}
