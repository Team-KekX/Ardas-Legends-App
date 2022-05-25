package com.ardaslegends.data.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class Coordinate {

    private Integer x;
    private Integer y;
    private Integer z;

}
