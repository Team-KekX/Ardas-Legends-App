package com.ardaslegends.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor

@MappedSuperclass
public abstract class AbstractEntity extends AbstractDomainObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Version
    @EqualsAndHashCode.Exclude
    private Integer version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
