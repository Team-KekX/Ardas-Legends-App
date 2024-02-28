package com.ardaslegends.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

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

    @NotNull
    private Boolean isMounted = false;

    @ManyToMany
    @JoinTable(name = "factions_units",
            joinColumns = { @JoinColumn(name = "unit_name", foreignKey = @ForeignKey(name = "fk_factions_units_unit_name"))},
            inverseJoinColumns = { @JoinColumn(name = "faction_id", foreignKey = @ForeignKey(name = "fk_factions_units_faction_id")) })
    private Set<Faction> usableBy = new HashSet<>(2);

    public UnitType(String unitName, Double tokenCost, Boolean isMounted) {
        this.unitName = unitName;
        this.tokenCost = tokenCost;
        this.isMounted = isMounted;
    }
}
