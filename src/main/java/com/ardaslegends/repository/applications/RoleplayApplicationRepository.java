package com.ardaslegends.repository.applications;

import com.ardaslegends.domain.applications.RoleplayApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleplayApplicationRepository extends JpaRepository<RoleplayApplication, Long> {

    public Slice<RoleplayApplication> findAllByAcceptedFalse(Pageable pageable);

    public Set<RoleplayApplication> findAllByAcceptedFalse();
}
