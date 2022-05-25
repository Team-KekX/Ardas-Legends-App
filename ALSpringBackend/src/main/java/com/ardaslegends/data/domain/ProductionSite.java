package com.ardaslegends.data.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "production_sites")
public class ProductionSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductionSiteType type; //unique, type of production site, e.g. FARM

    @Column(name = "produced_resource")
    private String producedResource; //the resource this production site produces

    @Column(name = "amount_produced")
    private Integer amountProduced; //the amount

}
