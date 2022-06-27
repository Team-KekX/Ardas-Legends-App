package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor

@Embeddable
public class Path {

    private final Integer cost;

    @ElementCollection
    private final List<String> path;

    public Path() {
       this.cost = 0;
       this.path = new ArrayList<>(1);
    }
}
