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

@Entity
@Table(name = "war_participants")
public class WarParticipant  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private War war;

    @ManyToOne
    private Faction warParticipant;

    private boolean initalParty;

    private LocalDateTime joiningDate;

}
