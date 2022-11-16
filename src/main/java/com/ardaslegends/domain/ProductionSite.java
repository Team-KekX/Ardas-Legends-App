package com.ardaslegends.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "production_sites")
public final class ProductionSite extends AbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductionSiteType type; //unique, type of production site, e.g. FARM

    @ManyToOne
    private Resource producedResource; //the resource this production site produces

    @Column(name = "amount_produced")
    private Integer amountProduced; //the amount

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionSite that = (ProductionSite) o;
        return type == that.type && producedResource.equals(that.producedResource) && amountProduced.equals(that.amountProduced);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, producedResource, amountProduced);
    }
}
