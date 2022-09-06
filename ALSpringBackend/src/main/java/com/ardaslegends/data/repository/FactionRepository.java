package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Faction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactionRepository extends JpaRepository<Faction, String> {
    Optional<Faction> findFactionByName(String name);
}
