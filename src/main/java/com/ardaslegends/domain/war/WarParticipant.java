package com.ardaslegends.domain.war;

import com.ardaslegends.domain.AbstractDomainEntity;
import com.ardaslegends.domain.Faction;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Embeddable
public class WarParticipant  {

    @ManyToOne
    private Faction warParticipant;

    private boolean initalParty;

    private LocalDateTime joiningDate;

}
