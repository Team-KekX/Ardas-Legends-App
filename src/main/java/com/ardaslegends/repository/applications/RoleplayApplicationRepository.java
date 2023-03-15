package com.ardaslegends.repository.applications;

import com.ardaslegends.domain.applications.RoleplayApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleplayApplicationRepository extends JpaRepository<RoleplayApplication, Long> { }
