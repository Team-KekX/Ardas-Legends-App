package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.ProductionSite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j

@Embeddable
public class EmbeddedProductionSite {
    @ManyToOne
    private ProductionSite productionSite;
    private Long count;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmbeddedProductionSite that = (EmbeddedProductionSite) o;
        return Objects.equals(productionSite, that.productionSite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionSite);
    }
}
