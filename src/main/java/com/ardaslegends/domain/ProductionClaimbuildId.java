package com.ardaslegends.domain;


import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


/***
 * This is the Id Mapping for the Production - Claimbuild Association Table
 * Without this, JPA doesn't map the primary key correctly
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public final class ProductionClaimbuildId extends AbstractDomainEntity implements Serializable {

    private static final long serialVersionUID = -7659401942823299559L;

    private Long productionSiteId;

    private String claimbuildId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionClaimbuildId that = (ProductionClaimbuildId) o;
        return Objects.equals(productionSiteId, that.productionSiteId) && Objects.equals(claimbuildId, that.claimbuildId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionSiteId, claimbuildId);
    }
}
