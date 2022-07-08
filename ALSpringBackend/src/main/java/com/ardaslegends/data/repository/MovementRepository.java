package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    //TODO add Test
    public Optional<Movement> findMovementByPlayer(Player player);
}
