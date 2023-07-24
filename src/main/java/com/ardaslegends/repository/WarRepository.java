package com.ardaslegends.repository;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.presentation.api.WarRestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
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
    Set<War> findAllWarsWithFaction(Faction faction);

    @Query("""
            select (count(w) > 0) from War w 
                left join w.aggressors aggressors 
                left join w.defenders defenders
            where (aggressors.warParticipant = ?1 and defenders.warParticipant = ?2)
            or (aggressors.warParticipant = ?2 and defenders.warParticipant = ?1)""")
    boolean isFactionAtWarWithOtherFaction(Faction faction, Faction otherFaction);

}
