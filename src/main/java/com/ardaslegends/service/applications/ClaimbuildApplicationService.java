package com.ardaslegends.service.applications;

import com.ardaslegends.domain.applications.ApplicationState;
import com.ardaslegends.domain.applications.ClaimbuildApplication;
import com.ardaslegends.repository.ClaimBuildRepository;
import com.ardaslegends.repository.player.PlayerRepository;
import com.ardaslegends.repository.applications.ClaimbuildApplicationRepository;
import com.ardaslegends.service.AbstractService;
import com.ardaslegends.service.dto.applications.CreateClaimbuildApplicationDto;
import com.ardaslegends.service.dto.player.DiscordIdDto;
import com.ardaslegends.service.exceptions.applications.ClaimbuildApplicationException;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class ClaimbuildApplicationService extends AbstractService<ClaimbuildApplication, ClaimbuildApplicationRepository> {

    private final ClaimbuildApplicationRepository cbAppRepository;
    private final ClaimBuildRepository claimBuildRepository;
    private final PlayerRepository playerRepository;

    @Transactional(readOnly = false)
    public ClaimbuildApplication createClaimbuildApplication(CreateClaimbuildApplicationDto dto) {
        log.debug("Creating ClaimbuildApplication with data [{}]", dto);
        Objects.requireNonNull(dto);

        ServiceUtils.checkAllNulls(dto);


        val builtByFutureList = Arrays.stream(dto.builtBy())
                .map(discordIdDto -> CompletableFuture.supplyAsync(() ->
                        secureFind(discordIdDto.discordId() ,playerRepository::findByDiscordID),
                        getExecutorService()))
                .toList();
        val builtByFuture = CompletableFuture.allOf(builtByFutureList.toArray(CompletableFuture[]::new));
        val optionalClaimbuild = secureFind(dto.claimbuildName(),claimBuildRepository::findClaimBuildByName);

        val applicantPlayer = secureFind(dto.applicant().discordId(), playerRepository::findByDiscordID);

        if(optionalClaimbuild.isPresent() ) {
            log.warn("Claimbuild with name [{}] already exists", dto.claimbuildName());
            throw ClaimbuildApplicationException.claibuildWithNameAlreadyExists(dto.claimbuildName());
        }

        val optionalClaimbuildApp = secureFind(dto.claimbuildName(), ApplicationState.OPEN, cbAppRepository::findByClaimbuildNameIgnoreCaseAndState);

        if(optionalClaimbuildApp.isPresent()) {
            log.warn("Claimbuild Application with name [{}] already exists", dto.claimbuildName());
            throw ClaimbuildApplicationException.claibuildApplicationWithNameAlreadyExists(dto.claimbuildName());
        }

        log.trace("Joining builtByFuture.allOf, then iterating over builtByResults");
        secureJoin(builtByFuture);

        // Joining Cfs, then getting all players where the Optional is not empty
        val foundPlayers = builtByFutureList.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        // Iterating over initial discordId which should be present in foundPlayers and mapping which Ids have not been found.
        List<String> notFoundPlayers = Arrays.stream(dto.builtBy())
                .filter(discordIdDto -> foundPlayers.stream().noneMatch(player -> player.getDiscordID().equals(discordIdDto.discordId())))
                .map(DiscordIdDto::discordId)
                .toList();


        return null;
    }
}
