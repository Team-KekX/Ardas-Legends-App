package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.AbstractEntity;
import com.ardaslegends.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractApplication extends AbstractEntity {

    @Getter
    @NotNull
    @PastOrPresent
    private LocalDateTime appliedAt;

    @Getter
    @NotNull
    private Short voteCount;

    @OneToMany
    @NotNull
    private Set<Player> acceptedBy;

    @Getter
    @NotNull
    @PastOrPresent
    private LocalDateTime acceptedAt;
    @Getter
    @NotNull
    private Boolean accepted;

    public AbstractApplication() {
        voteCount = 0;
        appliedAt = LocalDateTime.now();
        accepted = false;

        acceptedBy = new HashSet<>(3);
    }
    public Set<Player> getAcceptedBy() {
        return Collections.unmodifiableSet(acceptedBy);
    }

    public void addAcceptor(Player player) {
        acceptedBy.add(player);
        voteCount = (short) acceptedBy.size();
    }

    public void removeAccept(Player player) {
        acceptedBy.remove(player);
        voteCount = (short) acceptedBy.size();
    }
}
