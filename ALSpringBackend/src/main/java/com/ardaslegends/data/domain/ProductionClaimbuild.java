package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;


/***
 * Association Table for the Claimbuild 1:n - n:1 ProductionSite Association
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "production_claimbuild")
public class ProductionClaimbuild {

    @EmbeddedId
    private ProductionClaimbuildId id;

    @ManyToOne
    @MapsId("productionSiteId")
    @JoinColumn(name = "production_site_id", foreignKey = @ForeignKey(name = "fk_production_site_id"))
    private ProductionSite productionSite;

    @ManyToOne
    @MapsId("claimbuildId")
    @JoinColumn(name = "claimbuild_id", foreignKey = @ForeignKey(name = "fk_claimbuild_id"))
    private ClaimBuild claimbuild;

    private Long count;

}
