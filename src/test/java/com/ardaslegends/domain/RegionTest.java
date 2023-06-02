package com.ardaslegends.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class RegionTest {

    Faction gondor;
    Faction mordor;
    Set<ClaimBuild> cbSet;
    Set<Faction> factionSet;
    Region region;
    Faction factionWithNoCbInClaimedBy;
    @BeforeEach
    void setup() {

        gondor = Faction.builder().name("Gondor").build();
        mordor = Faction.builder().name("Mordor").build();
        factionWithNoCbInClaimedBy = Faction.builder().name("No Cb Faction").regions(new HashSet<>()).build();

        factionSet = new HashSet<>();
        factionSet.addAll(Set.of(mordor,gondor,factionWithNoCbInClaimedBy));

        ClaimBuild claimBuild1 = ClaimBuild.builder().name("kek1").ownedBy(gondor).build();
        ClaimBuild claimBuild2 = ClaimBuild.builder().name("kek2").ownedBy(gondor).build();
        ClaimBuild claimBuild3 = ClaimBuild.builder().name("kek3").ownedBy(gondor).build();
        ClaimBuild claimBuild4 = ClaimBuild.builder().name("kek4").ownedBy(gondor).build();
        ClaimBuild claimBuild5 = ClaimBuild.builder().name("kek5").ownedBy(gondor).build();

        ClaimBuild claimBuild6 = ClaimBuild.builder().name("kek6").ownedBy(mordor).build();
        ClaimBuild claimBuild7 = ClaimBuild.builder().name("kek7").ownedBy(mordor).build();
        ClaimBuild claimBuild8 = ClaimBuild.builder().name("kek8").ownedBy(mordor).build();

        cbSet = new HashSet<>(List.of(claimBuild1, claimBuild2, claimBuild3, claimBuild4, claimBuild5, claimBuild6, claimBuild7, claimBuild8));

        region = Region.builder().claimBuilds(cbSet).claimedBy(factionSet).build();
        factionWithNoCbInClaimedBy.getRegions().add(region);
    }

    @Test
    void ensureRemoveFactionFromClaimedByWorksProperly() {

        region.removeFactionFromClaimedBy(factionWithNoCbInClaimedBy);

        assertThat(region.getClaimedBy().contains(factionWithNoCbInClaimedBy)).isFalse();
        assertThat(region.isHasOwnershipChanged()).isTrue();
        assertThat(factionWithNoCbInClaimedBy.getRegions().contains(region)).isFalse();

    }

    @Test
    void ensureAddFactionToClaimedByWorksProperly() {

        Faction faction = Faction.builder().name("New Faction cope").regions(new HashSet<>()).build();

        region.addFactionToClaimedBy(faction);

        assertThat(region.getClaimedBy().contains(faction)).isTrue();
        assertThat(faction.getRegions().contains(region)).isTrue();
        assertThat(region.isHasOwnershipChanged()).isTrue();

    }

    @Test
    void ensureHasClaimbuildInRegionWorksProperly() {
        log.debug("Testing if hasClaimbuildInRegion works properly");

        var result = region.hasClaimbuildInRegion(gondor);

        assertThat(result).isTrue();

        var result2 = region.hasClaimbuildInRegion(Faction.builder().name("False2").build());

        assertThat(result2).isFalse();
    }

    @Test
    void ensureEqualsWorksCorrectly() {
        Region r1 = new Region("1", "one", null, null, null, null);
        Region r2 = new Region("1", "anotherOne", null, null, null, null);
        Region r3 = new Region("2", "anotherOne", null, null, null, null);

        assertThat(r1).isEqualTo(r2);
        assertThat(r2).isNotEqualTo(r3);

    }

    @Test
    void ensureThatGetNeighborsReturnsUnmodifiableSet() {
        Region r1 = new Region("1", "one", null, null, null, new HashSet<>());

        Set<Region> presumedUnmodifiableSet = r1.getNeighboringRegions();

        assertThat(presumedUnmodifiableSet).isNotNull();
        assertThrows(UnsupportedOperationException.class, () -> presumedUnmodifiableSet.add(new Region()));

    }

    @Test
    void ensureThatAddNeihborsWorksCorrectly() {
        Region r1 = new Region("1", "one", null, null, null, new HashSet<>());
        Region r2 = new Region("2", "two", null, null, null, new HashSet<>());

        var neighbors = r1.getNeighboringRegions();

        assertThat(neighbors).isNotNull();
        assertThat(neighbors.size()).isEqualTo(0);

        r1.addNeighbour(r2);

        assertThat(neighbors.size()).isEqualTo(1);
    }

    @Test
    void ensureThatAddNeihborsDoesntAddIfItContainsTheObject() {
        Region r1 = new Region("1", "one", null, null, null, new HashSet<>());
        Region r2 = new Region("2", "two", null, null, null, new HashSet<>());

        var neighbors = r1.getNeighboringRegions();

        r1.addNeighbour(r2);

        assertThat(neighbors).isNotNull();
        assertThat(neighbors.size()).isEqualTo(1);

        assertThat(r1.addNeighbour(r2)).isFalse();
    }

    @Test
    void ensureThatAddNeihborsThrowsNPEWhenParameterIsNull() {
        Region r1 = new Region("1", "one", null, null, null, new HashSet<>());

        var neighbors = r1.getNeighboringRegions();

        assertThrows(NullPointerException.class,  () -> r1.addNeighbour(null));

        assertThat(neighbors).isNotNull();
        assertThat(neighbors.size()).isEqualTo(0);
    }

    @Test
    void ensureGetCostWorksCorrectly() {
        Region r1 = new Region("1", "one", RegionType.HILL, null, null, new HashSet<>());

        assertThat(r1.getCost()).isEqualTo(RegionType.HILL.getCost());
    }

}
