package com.ardaslegends.data.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@Builder

@Embeddable
public class Path {

    private Integer cost;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> path = new ArrayList<>();

    public Path() {
       this.cost = 0;
       this.path = new ArrayList<>(1);
    }

    public String getDestination() {
        return path.get(path.size()-1);
    }
    public String getStart() {return path.get(0);}

    public int getCostInHours() { return cost * 24; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path1 = (Path) o;
        return cost.equals(path1.cost) && path.equals(path1.path);
    }

    @Override
    public int hashCode() {
        return path != null ? Objects.hash(cost, path):0;
    }
}
