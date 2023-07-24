package com.ardaslegends.repository.applications;

import com.ardaslegends.domain.applications.ApplicationState;
import com.ardaslegends.domain.applications.RoleplayApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleplayApplicationRepository extends JpaRepository<RoleplayApplication, Long> {

    @Query("select r from RoleplayApplication r where r.state = ?1")
    Slice<RoleplayApplication> findByState(ApplicationState state, Pageable pageable);

    @Query("select r from RoleplayApplication r where r.state = ?1")
    public Set<RoleplayApplication> findByState(ApplicationState state);
}
