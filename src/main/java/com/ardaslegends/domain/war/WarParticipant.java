package com.ardaslegends.domain.war;

import com.ardaslegends.domain.Faction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class WarParticipant  {

    @ManyToOne
    @JoinColumn(name = "participant_faction_id")
    @NotNull
    private Faction warParticipant;

    @NotNull
    private Boolean initialParty;

    @NotNull
    private LocalDateTime joiningDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarParticipant that = (WarParticipant) o;
        return Objects.equals(warParticipant, that.warParticipant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warParticipant);
    }
}
