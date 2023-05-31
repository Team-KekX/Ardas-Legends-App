package com.ardaslegends.repository.applications.claimbuildapp;

import com.ardaslegends.domain.applications.ApplicationState;
import com.ardaslegends.domain.applications.ClaimbuildApplication;
import com.ardaslegends.domain.applications.QClaimbuildApplication;
import com.ardaslegends.repository.exceptions.ClaimbuildApplicationRepositoryException;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Optional;

public class ClaimbuildApplicationRepositoryImpl extends QuerydslRepositorySupport implements ClaimbuildApplicationRepositoryCustom {
    public ClaimbuildApplicationRepositoryImpl() {
        super(ClaimbuildApplication.class);
    }

    @Override
    public @NonNull ClaimbuildApplication queryByNameIgnoreCaseAndState(@NonNull String claimbuildName, @NonNull ApplicationState state) {
        val claimbuildApp = queryByNameIgnoreCaseAndStateOptional(claimbuildName, state);

        if(claimbuildApp.isEmpty()) {
            throw ClaimbuildApplicationRepositoryException
                    .entityNotFound("(claimbuildName, state)", "(" + claimbuildName + ", " + state.displayName + ")");
        }

        return claimbuildApp.get();
    }

    @Override
    public Optional<ClaimbuildApplication> queryByNameIgnoreCaseAndStateOptional(String claimbuildName, ApplicationState state) {
        Objects.requireNonNull(claimbuildName);
        Objects.requireNonNull(state);
        val qclaimbuildApp = QClaimbuildApplication.claimbuildApplication;

        val claimbuildApp = from(qclaimbuildApp)
                .where(qclaimbuildApp.claimbuildName.equalsIgnoreCase(claimbuildName).and(qclaimbuildApp.state.eq(state)))
                .fetchFirst();

        return Optional.ofNullable(claimbuildApp);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndState(String claimbuildName, ApplicationState state) {
        return queryByNameIgnoreCaseAndStateOptional(claimbuildName, state).isPresent();
    }
}
