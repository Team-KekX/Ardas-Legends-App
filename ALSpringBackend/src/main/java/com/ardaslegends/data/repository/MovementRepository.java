package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    //TODO add Test
}
