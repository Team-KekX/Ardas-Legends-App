package com.ardaslegends.domain.war;

import com.ardaslegends.domain.AbstractEntity;
import com.ardaslegends.domain.Faction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "war_participants")
public class WarParticipant extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "participant_faction_id")
    @NotNull
    private Faction warParticipant;

    @NotNull
    private Boolean initialParty;

    @NotNull
    private LocalDateTime joiningDate;

    @NotNull
    private WarInvolvement involvment;
}
