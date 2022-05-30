package com.ardaslegends.domain;

import com.ardaslegends.data.domain.Region;
import com.ardaslegends.data.domain.RegionType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegionTest {

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
