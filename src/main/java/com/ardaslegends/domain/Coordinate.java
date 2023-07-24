package com.ardaslegends.domain;

import lombok.*;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class Coordinate {

    private Integer x;
    private Integer y;
    private Integer z;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x.equals(that.x) && y.equals(that.y) && z.equals(that.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }
}
