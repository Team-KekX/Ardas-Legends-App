package com.ardaslegends.repository;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarRepository extends JpaRepository<War, Long> {
    public Optional<War> findByName(String name);

    @Query("""
        FROM War 
        where War.aggressors.warParticipant = :aggressor 
        """)
    public List<War> findAllWarsWithAggressor(@Param("aggressor") Faction aggressor);
}
