package com.ardaslegends.alspringbackend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "unit_type", foreignKey = @ForeignKey(name = "fk_unit_type"))
    private UnitType unitType; //The kind of unit, e.g. Gondor Soldier
    private Integer count; //maximum aamount of those units that are in the army
    private Integer amountAlive; //current alive soldiers


}
