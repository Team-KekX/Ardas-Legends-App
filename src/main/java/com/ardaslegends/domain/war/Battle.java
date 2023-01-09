package com.ardaslegends.domain.war;


import com.ardaslegends.domain.AbstractDomainEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "battles")
public class Battle extends AbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private War war;

    // TODO: Missing battle participants

    private LocalDateTime declaredDate;

    private LocalDateTime timeFrozenFrom;

    private LocalDateTime timeFrozenUntil;

    private LocalDateTime agreedBattleDate;

    @Embedded
    private BattleLocation battleLocation;

}
