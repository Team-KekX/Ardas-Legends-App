package com.ardaslegends.repository;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.Movement;
import com.ardaslegends.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    //TODO add Test
    public List<Movement> findMovementsByPlayer(Player player);
    public Optional<Movement> findMovementByArmyAndIsCurrentlyActiveTrue(Army army);
    public Optional<Movement> findMovementByPlayerAndIsCurrentlyActiveTrue(Player player);
    public List<Movement> findMovementsByIsCurrentlyActive(Boolean isActive);
}
