package com.ardaslegends.repository.applications;

import com.ardaslegends.domain.applications.ClaimbuildApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimbuildApplicationRepository extends JpaRepository<ClaimbuildApplication, Long> {

}
