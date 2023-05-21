package com.ardaslegends.repository.faction;

import com.ardaslegends.domain.Faction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactionRepository extends JpaRepository<Faction, Long> {
    Optional<Faction> findFactionByName(String name);
    Optional<Faction> findFactionByFactionRoleId(Long factionRoleId);
}
