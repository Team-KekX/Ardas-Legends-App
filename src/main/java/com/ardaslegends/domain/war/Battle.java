package com.ardaslegends.domain.war;


import com.ardaslegends.domain.AbstractDomainObject;
import com.ardaslegends.domain.Army;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "battles")
public class Battle extends AbstractDomainObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "war_id", foreignKey = @ForeignKey(name = "fk_battle_war_id"))
    private War war;

    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "battle_attackingArmies",
            joinColumns = { @JoinColumn(name = "battle_id", foreignKey = @ForeignKey(name = "fk_battle_attackingArmies_battle"))},
            inverseJoinColumns = { @JoinColumn(name = "atackingArmy_id", foreignKey = @ForeignKey(name = "fk_battle_attackingArmies_attackingArmy")) })
    private Set<Army> attackingArmies = new HashSet<>(1);

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "battle_defendingArmies",
            joinColumns = { @JoinColumn(name = "battle_id", foreignKey = @ForeignKey(name = "fk_battle_defendingArmies_battle"))},
            inverseJoinColumns = { @JoinColumn(name = "defendingArmy_id", foreignKey = @ForeignKey(name = "fk_battle_defendingArmies_defendingArmy")) })
    private Set<Army> defendingArmies = new HashSet<>();

    private LocalDateTime declaredDate;

    private LocalDateTime timeFrozenFrom;

    private LocalDateTime timeFrozenUntil;

    private LocalDateTime agreedBattleDate;

    @Embedded
    private BattleLocation battleLocation;

    public Battle(War war, String name, Set<Army> attackingArmies, Set<Army> defendingArmies, LocalDateTime declaredDate, LocalDateTime timeFrozenFrom, LocalDateTime timeFrozenUntil, LocalDateTime agreedBattleDate, BattleLocation battleLocation) {
        this.war = war;
        this.name = name;
        this.attackingArmies = new HashSet<>(attackingArmies);
        this.defendingArmies = new HashSet<>(defendingArmies);
        this.declaredDate = declaredDate;
        this.timeFrozenFrom = timeFrozenFrom;
        this.timeFrozenUntil = timeFrozenUntil;
        this.agreedBattleDate = agreedBattleDate;
        this.battleLocation = battleLocation;
    }
}
