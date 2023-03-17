package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.AbstractEntity;
import com.ardaslegends.domain.Player;
import com.ardaslegends.service.exceptions.applications.RoleplayApplicationServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractApplication extends AbstractEntity {
    private final short REQUIRED_VOTES = 2;

    @Getter
    @NotNull
    @PastOrPresent
    private LocalDateTime appliedAt;

    @Getter
    @NotNull
    private Short voteCount;

    @Getter
    @NotNull
    private LocalDateTime lastVoteAt;

    @OneToMany
    @NotNull
    private Set<Player> acceptedBy;

    @Getter
    @PastOrPresent
    private LocalDateTime acceptedAt;
    @Getter
    @NotNull
    private Boolean accepted;

    public AbstractApplication() {
        voteCount = 0;
        appliedAt = LocalDateTime.now();
        lastVoteAt = LocalDateTime.now();
        accepted = false;

        acceptedBy = new HashSet<>(3);
    }
    public Set<Player> getAcceptedBy() {
        return Collections.unmodifiableSet(acceptedBy);
    }

    public void addAcceptor(Player player) {
        if(!player.getIsStaff()) {
            log.warn("Player [{}] cannot vote because they are not staff", player.getIgn());
            throw RoleplayApplicationServiceException.playerIsNotStaff(player.getIgn());
        }

        val success = acceptedBy.add(player);
        if(!success){
            log.warn("Player [{}] already added their vote to the application", player.getIgn());
            throw RoleplayApplicationServiceException.playerAlreadyVoted(player.getIgn());
        }
        voteCount = (short) acceptedBy.size();
        lastVoteAt = LocalDateTime.now();
    }

    public void removeAccept(Player player) {
        val success = acceptedBy.remove(player);
        if(!success) {
            log.warn("Player [{}] did not vote on the application", player.getIgn());
            throw RoleplayApplicationServiceException.playerDidNotVote(player.getIgn());
        }
        voteCount = (short) acceptedBy.size();
    }

    public boolean acceptable() {
        return voteCount >= REQUIRED_VOTES;
    }
    protected void setAccepted() {
        acceptedAt = LocalDateTime.now();
        accepted = true;
    }
}
