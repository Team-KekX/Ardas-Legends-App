package com.ardaslegends.repository.claimbuild;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.QClaimBuild;
import com.ardaslegends.repository.exceptions.ClaimbuildRepositoryException;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class ClaimbuildRepositoryImpl extends QuerydslRepositorySupport implements ClaimbuildRepositoryCustom {
    public ClaimbuildRepositoryImpl() {
        super(ClaimBuild.class);
    }

    @Override
    public ClaimBuild queryByNameIgnoreCase(String claimbuildName) {
        val fetchedClaimbuild = queryByNameIgnoreCaseOptional(claimbuildName);

        if(fetchedClaimbuild.isEmpty()) {
            throw ClaimbuildRepositoryException.entityNotFound("claimbuildName", claimbuildName);
        }

        return fetchedClaimbuild.get();
    }

    @Override
    public Optional<ClaimBuild> queryByNameIgnoreCaseOptional(String claimbuildName) {
        Objects.requireNonNull(claimbuildName);
        QClaimBuild qClaimBuild = QClaimBuild.claimBuild;

        val fetchedClaimbuild = from(qClaimBuild)
                .where(qClaimBuild.name.equalsIgnoreCase(claimbuildName))
                .fetchFirst();

        return Optional.of(fetchedClaimbuild);
    }

    @Override
    public boolean existsByNameIgnoreCase(String claimbuildName) {
        return queryByNameIgnoreCaseOptional(claimbuildName).isPresent();
    }


}
