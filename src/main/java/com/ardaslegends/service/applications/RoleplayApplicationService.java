package com.ardaslegends.service.applications;

import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.applications.ApplicationState;
import com.ardaslegends.domain.applications.RoleplayApplication;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.repository.FactionRepository;
import com.ardaslegends.repository.player.PlayerRepository;
import com.ardaslegends.repository.applications.RoleplayApplicationRepository;
import com.ardaslegends.service.AbstractService;
import com.ardaslegends.service.dto.applications.CreateRpApplicatonDto;
import com.ardaslegends.service.dto.applications.RpApplicationVoteDto;
import com.ardaslegends.service.exceptions.FactionServiceException;
import com.ardaslegends.service.exceptions.PlayerServiceException;
import com.ardaslegends.service.exceptions.applications.RoleplayApplicationServiceException;
import com.ardaslegends.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class RoleplayApplicationService extends AbstractService<RoleplayApplication, RoleplayApplicationRepository> {

    private final RoleplayApplicationRepository rpRepository;
    private final FactionRepository factionRepository;
    private final PlayerRepository playerRepository;
    private final BotProperties botProperties;
    private final UrlValidator urlValidator;
    private final Clock clock;

    public Slice<RoleplayApplication> findAll(Pageable pageable) {
        log.debug("Fetching slice of all rpApplications [{}]", pageable);
        Objects.requireNonNull(pageable);

        val applications = secureFind(pageable, rpRepository::findAll);
        log.debug("Fetched active rpApplications [{}]", applications);

        return applications;
    }

    public Slice<RoleplayApplication> findAllActive(Pageable pageable) {
        log.debug("Fetching slice of active roleplay applications [{}]", pageable);
        Objects.requireNonNull(pageable);

        val applications = secureFind(ApplicationState.OPEN ,pageable, rpRepository::findByState);
        log.debug("Fetched active rpApplications [{}]", applications);

        return applications;
    }

    @Transactional(readOnly = false)
    public RoleplayApplication createRpApplication(CreateRpApplicatonDto dto) {
        log.debug("Creating RpApplication with data [{}]", dto);
        Objects.requireNonNull(dto);

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

       if(!urlValidator.isValid(dto.linkToLore())){
           log.warn("Given url [{}] is not a valid URL", dto.linkToLore());
           throw RoleplayApplicationServiceException.urlIsNotValid(dto.linkToLore());
       }

       val optionalPlayer = secureFind(dto.discordId(), playerRepository::findByDiscordID);
       val optionalFaction = secureFind(dto.factionName(), factionRepository::findFactionByName);

       if (optionalFaction.isEmpty()) {
           log.warn("Faction with name [{}] was not found", dto.factionName());
           throw FactionServiceException.noFactionWithNameFoundAndAll(dto.factionName());
       }

       val faction = optionalFaction.get();

       if (optionalPlayer.isEmpty()) {
           log.warn("Player with discordId [{}] is required but was not found", dto.discordId());
           throw PlayerServiceException.noPlayerFound(dto.discordId());
       }

       val player = optionalPlayer.get();

       var application = new RoleplayApplication(player, faction, dto.characterName(), dto.characterTitle(), dto.characterReason(), dto.gear(), dto.pvpPreference(), dto.linkToLore());
       val applicationMessage = application.sendApplicationMessage(botProperties.getRpAppsChannel());

       try {
           application = secureSave(application, rpRepository);
       }
       catch (Exception e) {
           applicationMessage.delete("Failed to save application into Database therefore deleting application message in discord").join();
           throw e;
       }

       log.info("Successfully created rpApplication [{}]", application);
       return application;
    }

    @Transactional(readOnly = false)
    public RoleplayApplication addVote(RpApplicationVoteDto dto) {
        log.debug("Adding Vote to application [{}]", dto);
        Objects.requireNonNull(dto);

        ServiceUtils.checkAllNulls(dto);

        var application = getRoleplayApplication(dto);
        val player = getPlayer(dto);

        application.addAcceptor(player);

        application = secureSave(application, rpRepository);
        log.info("Added vote to application [{}]", application);

        return application;
    }

    @Transactional(readOnly = false)
    public RoleplayApplication removeVote(RpApplicationVoteDto dto) {
        log.debug("Removing vote from application [{}]", dto);
        Objects.requireNonNull(dto);

        ServiceUtils.checkAllNulls(dto);

        var application = getRoleplayApplication(dto);
        val player = getPlayer(dto);

        application.removeAccept(player);

        application = secureSave(application, rpRepository);
        log.info("Removed vote from application [{}]", application);

        return application;
    }

    private Player getPlayer(RpApplicationVoteDto dto) {
        val optionalPlayer = secureFind(dto.discordId(), playerRepository::findByDiscordID);

        if(optionalPlayer.isEmpty()) {
            log.warn("No player found with discordId [{}]", dto.discordId());
            throw PlayerServiceException.noPlayerFound(dto.discordId());
        }
        return optionalPlayer.get();
    }

    private RoleplayApplication getRoleplayApplication(RpApplicationVoteDto dto) {
        val optionalApplication = secureFind(dto.applicationId(), rpRepository::findById);

        if(optionalApplication.isEmpty()) {
            log.warn("No rp application found with id [{}]", dto.applicationId());
            throw RoleplayApplicationServiceException.noApplicationFoundWithId(dto.applicationId());
        }
        return optionalApplication.get();
    }

    @Async
    @Scheduled(cron = "0 */15 * ? * *")
    @Transactional(readOnly = false)
    public void handleOpenRoleplayApplications() {
        val startDateTime = LocalDateTime.now(clock);
        long startNanos = System.nanoTime();
        log.debug("Starting scheduled handling of open roleplay applications - System time: [{}]", startDateTime);

        log.debug("Fetching all open roleplay-applications");
        secureFind(ApplicationState.OPEN, rpRepository::findByState).stream()
                .filter(RoleplayApplication::acceptable)
                .forEach(accept());

        long endNanos = System.nanoTime();
        log.info("Finished handling open roleplay-application [Time: {}, Amount accepted: {}]", TimeUnit.NANOSECONDS.toMillis(endNanos-startNanos));
    }

    private Consumer<RoleplayApplication> accept() {
        return application -> {
            val message = application.sendAcceptedMessage(botProperties.getRpAppsChannel());
            val character = application.accept();
            val player = application.getApplicant();
            player.setRpChar(character);

            try {
                secureSave(application, rpRepository);
                log.info("Accepted rp application from [{}]", player.getIgn());
            } catch (Exception e) {
                message.delete("Failed to update application to accepted in database therefore deleting message");
                throw e;
            }
        };
    }

}
