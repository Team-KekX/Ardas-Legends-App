package com.ardaslegends.domain.war;


import com.ardaslegends.domain.AbstractDomainEntity;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter

@Entity
@Table(name = "battles")
public class Battle extends AbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private War war;

    private String name;

    // TODO: Missing battle participants

    private LocalDateTime declaredDate;

    private LocalDateTime timeFrozenFrom;

    private LocalDateTime timeFrozenUntil;

    private LocalDateTime agreedBattleDate;

    @Embedded
    private BattleLocation battleLocation;

}
