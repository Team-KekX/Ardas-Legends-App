package com.ardaslegends.domain.war;

import com.ardaslegends.domain.AbstractDomainObject;
import com.ardaslegends.domain.Faction;
import com.ardaslegends.service.exceptions.WarServiceException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j

@Entity
@Table(name = "wars")
public class War extends AbstractDomainObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @OneToMany
    private Set<WarParticipant> warParticipants = new HashSet<>(2);

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @OneToMany(mappedBy = "war")
    private Set<Battle> battles = new HashSet<>(4);

    // TODO: Test worthy
    public War(String name, Faction aggressor, Faction defender) {
        Objects.requireNonNull(name, "WarConstructor: Name must not be null");
        Objects.requireNonNull(aggressor, "WarConstructor: Aggressor must not be null");
        Objects.requireNonNull(defender, "WarConstructor: Defender must not be null");

        var warDeclarationDate = LocalDateTime.now();

        log.trace("Creating Aggressor WarParticipantObject");
        WarParticipant aggressorWarParticipant = new WarParticipant(aggressor, true, warDeclarationDate, WarInvolvement.ATTACKING);

        log.trace("Creating Defender WarParticipantObject");
        WarParticipant defenderWarParticipant = new WarParticipant(defender, true, warDeclarationDate, WarInvolvement.DEFENDING);

        this.name = name;
        this.warParticipants.add(aggressorWarParticipant);
        this.warParticipants.add(defenderWarParticipant);

        this.startDate = warDeclarationDate;
    }

    @NotNull
    public Set<WarParticipant> getEnemies(Faction faction) {
        AtomicReference<WarInvolvement> enemyInvolvement = new AtomicReference<>();
        warParticipants.stream()
                .filter(participant -> participant.getWarParticipant().equals(faction))
                .findFirst()
                .ifPresentOrElse(it -> {
                    WarInvolvement involvement = it.getInvolvement() == WarInvolvement.ATTACKING ? WarInvolvement.DEFENDING : WarInvolvement.ATTACKING;
                    enemyInvolvement.set(involvement);
                }, () -> {
                    throw new RuntimeException("Faction %s is not participating in the war '%s'".formatted(faction.getName(), this.name));
                });

        return warParticipants.stream()
                .filter(warParticipant -> warParticipant.getInvolvement().equals(enemyInvolvement.get()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public WarParticipant getInitialAttacker() {
        return getInitialParty(WarInvolvement.ATTACKING);
    }

    public WarParticipant getInitialDefender() {
        return getInitialParty(WarInvolvement.DEFENDING);
    }

    private WarParticipant getInitialParty(WarInvolvement involvement) {
        Objects.requireNonNull(involvement);
        return warParticipants.stream()
                .filter(participant -> participant.getInvolvement().equals(involvement) && participant.getInitialParty())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Found no initialParty in War '%s'".formatted(this.name)));
    }

    private <T> void addToSet(Set<T> set, T object, WarServiceException exception) {
        var successfullyAdded = set.add(object);

        if(!successfullyAdded) {
            log.warn("Could not add {} [{}] because it is already present in Set", object.getClass().getSimpleName(), object.toString());
            throw exception;
        }
    }

    public void addToAggressors(WarParticipant participant) {
    }

    public void addToDefenders() {

    }

    private void addParticipant(Faction faction, WarInvolvement involvement) {
        warParticipants.stream()
                .filter(warParticipant -> warParticipant.getWarParticipant().equals(faction))
                .findFirst()
                .ifPresent(it -> { throw WarServiceException.factionAlreadyJoinedTheWar(faction.getName(), it.getInvolvement()); });

        log.debug("Adding faction [{}] as a new participant in war [{}]", faction.getName(), this.name);
        val newParticipant = new WarParticipant(faction, false, LocalDateTime.now(), involvement);

        this.warParticipants.add(newParticipant);
    }

    public void addToBattles(Battle battle) {
        addToSet(this.battles, battle, WarServiceException.battleAlreadyListed(battle.getName()));
    }

    public Set<WarParticipant> getAggressors() {
        return this.warParticipants.stream()
                .filter(warParticipant -> warParticipant.getInvolvement().equals(WarInvolvement.ATTACKING))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<WarParticipant> getDefenders() {
        return this.warParticipants.stream()
                .filter(warParticipant -> warParticipant.getInvolvement().equals(WarInvolvement.DEFENDING))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Battle> getBattles() {
        return Collections.unmodifiableSet(this.battles);
    }
}
