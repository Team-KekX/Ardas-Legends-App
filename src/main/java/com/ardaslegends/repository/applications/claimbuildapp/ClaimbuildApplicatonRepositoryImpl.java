package com.ardaslegends.repository.applications.claimbuildapp;

import com.ardaslegends.domain.applications.ClaimbuildApplication;
import com.ardaslegends.domain.applications.QClaimbuildApplication;
import com.ardaslegends.repository.exceptions.ClaimbuildApplicationException;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class ClaimbuildApplicatonRepositoryImpl extends QuerydslRepositorySupport implements ClaimbuildApplicationRepositoryCustom {
    public ClaimbuildApplicatonRepositoryImpl() {
        super(ClaimbuildApplication.class);
    }

    @Override
    public @NonNull ClaimbuildApplication queryByName(@NonNull String claimbuildName) {
        Objects.requireNonNull(claimbuildName);
        val qclaimbuildApp = QClaimbuildApplication.claimbuildApplication;

        val claimbuildApp = from(qclaimbuildApp)
                .where(qclaimbuildApp.claimbuildName.eq(claimbuildName))
                .fetchFirst();

        if(claimbuildApp == null) { throw ClaimbuildApplicationException.entityNotFound("claimbuildName", claimbuildName); }

        return claimbuildApp;
    }
}
