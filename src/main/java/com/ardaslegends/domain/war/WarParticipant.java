package com.ardaslegends.domain.war;

import com.ardaslegends.domain.Faction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class WarParticipant  {

    @ManyToOne
    @JoinColumn(name = "participant_faction_id", foreignKey = @ForeignKey(name = "fk_faction_war_participant"))
    private Faction warParticipant;

    private boolean initialParty;

    private LocalDateTime joiningDate;

}
