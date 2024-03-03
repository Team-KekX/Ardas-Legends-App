package com.ardaslegends.repository;

import com.ardaslegends.domain.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, String>, UnitTypeRepositoryCustom {
}
