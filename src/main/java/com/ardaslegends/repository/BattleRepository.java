package com.ardaslegends.repository;

import com.ardaslegends.domain.war.Battle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleRepository extends JpaRepository<Battle, Long> {
}
