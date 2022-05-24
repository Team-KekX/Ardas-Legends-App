package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.Embeddable;

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

}
