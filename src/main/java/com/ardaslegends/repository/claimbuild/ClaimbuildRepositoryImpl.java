package com.ardaslegends.repository.claimbuild;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.QClaimBuild;
import com.ardaslegends.repository.exceptions.ClaimbuildRepositoryException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Repository
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
        Objects.requireNonNull(claimbuildName, "Claimbuild Name must not be null!");
        QClaimBuild qClaimBuild = QClaimBuild.claimBuild;

        val fetchedClaimbuild = from(qClaimBuild)
                .where(qClaimBuild.name.equalsIgnoreCase(claimbuildName))
                .fetchFirst();

        return Optional.ofNullable(fetchedClaimbuild);
    }

    @Override
    public boolean existsByNameIgnoreCase(String claimbuildName) {
        log.trace("Checking if a claimbuild with name '%s' already exists");
        return queryByNameIgnoreCaseOptional(claimbuildName).isPresent();
    }

    @Override
    public List<ClaimBuild> findClaimBuildsByNames(String[] names) {
        log.debug("Querying claimbuilds by names: {}", names);
        Objects.requireNonNull(names, "Names must not be null");
        QClaimBuild qClaimBuild = QClaimBuild.claimBuild;

        log.trace("Executing query");
        val fetchedClaimbuilds = from(qClaimBuild)
                .where(qClaimBuild.name.in(names))
                .stream().toList();

        log.debug("Queried claimbuilds: [{}]", fetchedClaimbuilds);

        return fetchedClaimbuilds;
    }


}
