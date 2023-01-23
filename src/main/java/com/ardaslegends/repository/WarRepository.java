package com.ardaslegends.repository;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WarRepository extends JpaRepository<War, Long> {
    public Optional<War> findByName(String name);

    @Query("""
            select w from War w 
                left join w.aggressors aggressors 
                left join w.defenders defenders
            where aggressors.warParticipant = ?1
            or defenders.warParticipant = ?1""")
    public Set<War> findAllWarsWithFaction(Faction faction);
}
