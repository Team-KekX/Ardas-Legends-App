package com.ardaslegends.domain;

import lombok.*;

import jakarta.persistence.*;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class PathElement {

    private Integer actualCost;
    private Integer baseCost;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Region region;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathElement that = (PathElement) o;
        return Objects.equals(actualCost, that.actualCost) && Objects.equals(baseCost, that.baseCost) && Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actualCost, baseCost, region);
    }

    @Override
    public String toString() {
        return "PathElement{" +
                "region=" + region.getId() +
                ", actualCost=" + actualCost +
                '}';
    }

    public boolean hasRegion(Region region) {
        return this.region.equals(region);
    }
}
