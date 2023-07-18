package com.ardaslegends.repository;

import com.ardaslegends.domain.*;
import com.ardaslegends.repository.player.PlayerRepository;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
@ActiveProfiles("test")
public class PlayerRepositoryTest {

    @Autowired
    PlayerRepository repository;

    @BeforeEach
    void setup() {
        val region = Region.builder().name("").id("1").regionType(RegionType.DESERT).build();
        val region2 = Region.builder().name("").id("2").regionType(RegionType.DESERT).build();
        val region3 = Region.builder().name("").id("3").regionType(RegionType.DESERT).build();
        val region4 = Region.builder().name("").id("4").regionType(RegionType.DESERT).build();
        val region5 = Region.builder().name("").id("5").regionType(RegionType.DESERT).build();
        Player p1 = Player.builder().ign("mirak").discordID("MiraksId").faction(Faction.builder().name("Gondor").homeRegion(region).build()).uuid("MiraksUUID").build();
        Player p2 = Player.builder().ign("vernon").discordID("vernonId").faction(Faction.builder().name("Mordor").homeRegion(region2).build()).uuid("vernonUUID").build();
        Player p3 = Player.builder().ign("luk").discordID("luksId").faction(Faction.builder().name("Arnor").homeRegion(region3).build()).uuid("luksUUID").build();
        Player p4 = Player.builder().ign("aned").discordID("anedsId").faction(Faction.builder().name("Rivendell").homeRegion(region4).build()).uuid("anedsUUID").build();
        Player p5 = Player.builder().ign("anotherOne").discordID("anotherOnesId").faction(Faction.builder().name("SecretNewFac").homeRegion(region5).build()).uuid("anotherOneUUID").build();;
        RPChar rpChar = new RPChar(p5, "Sauron", "s","s", true, null);
        p5.addActiveRpChar(rpChar);

        repository.saveAll(List.of(p1,p2,p3,p4,p5));
    }

    @Test
    void ensureFindByDiscordIdWorks() {

        // Assign
        String discordId = "MiraksId";

        // Act
        var query = repository.findByDiscordID(discordId);

        // Assert
        assertThat(query.isPresent()).isTrue();
        assertThat(query.get().getDiscordID()).isEqualTo(discordId);
    }

    @Test
    void ensureQueryByDiscordIdWorks() {
        String discordId=  "MiraksId";

        val query = repository.queryByDiscordId(discordId);

        assertThat(query).isNotNull();
        assertThat(query.getDiscordID()).isEqualTo(discordId);
    }

    @Test
    void ensureQueryByDiscordIdWithArraysWorks() {
        String[] discordIds = { "MiraksId", "vernonId", null, null, "luksId", "testIfFails" };

        var query = repository.queryAllByDiscordIds(discordIds);

        assertThat(query).isNotNull();
        assertThat(query.size()).isEqualTo(3);
    }

    @Test
    void ensureFindByIgnWorks() {
        // Assign
        String ign = "mirak";

        // Act
        var result = repository.findPlayerByIgn(ign);

        // Assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getDiscordID()).isEqualTo("MiraksId");
    }

    @Test
    void ensureFindByRPCharWorks() {
        // Assign

        val rpChar2 = RPChar.builder().name("Sauron").title("e").gear("e").pvp(true).build();

        // Act
        var result = repository.queryPlayerByRpChar(rpChar2.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getActiveCharacter().get().getGear()).isNotEqualTo(rpChar2.getGear());

    }

    @Test
    void ensureFindByRPCharReturnsEmptyOptionalWhenNothingFound() {
        // Assign

        val rpChar2 = RPChar.builder().name("SSomebodyThatIUsedToKnoooow").title("e").gear("e").pvp(true).build();

        // Act
        var result = repository.queryPlayerByRpChar(rpChar2.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();

    }
}
