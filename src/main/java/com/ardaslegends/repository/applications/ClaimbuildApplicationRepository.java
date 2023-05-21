package com.ardaslegends.repository.applications;

import com.ardaslegends.domain.applications.ApplicationState;
import com.ardaslegends.domain.applications.ClaimbuildApplication;
import com.ardaslegends.repository.applications.claimbuildapp.ClaimbuildApplicationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimbuildApplicationRepository extends JpaRepository<ClaimbuildApplication, Long>, ClaimbuildApplicationRepositoryCustom {
    Optional<ClaimbuildApplication> findByClaimbuildNameIgnoreCaseAndState(@NonNull String claimbuildName, @NonNull ApplicationState state);

}
