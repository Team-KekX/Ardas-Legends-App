package com.ardaslegends.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "unit_types")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "unitName")
public final class UnitType extends AbstractDomainObject {

    @Id
    private String unitName; //unique, the name of this Unit
    @NotNull(message = "UnitType: tokenCost must not be null")
    private Double tokenCost; //how much tokens this unit costs

}
