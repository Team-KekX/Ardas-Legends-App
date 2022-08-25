package com.ardaslegends.data.domain;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "armies")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name")
public final class Army extends AbstractDomainEntity {

    @Id
    private String name; //unique, the army's name

    @Enumerated(EnumType.STRING)
    @Column(name = "army_type")
    @NotNull(message = "Army: Army Type must not be null or empty")
    private ArmyType armyType; //type of the army, either ARMY, TRADING_COMPANY or ARMED_TRADERS

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction"))
    @NotNull(message = "Army: Faction must not be null")
    private Faction faction; //the faction this army belongs to

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "current_Region", foreignKey = @ForeignKey(name = "fk_current_region"))
    @NotNull(message = "Army: Region must not be null")
    private Region currentRegion; //region the army is currently in

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "rpChar.boundTo")
    @JoinColumn(name = "bound_to", foreignKey = @ForeignKey(name = "fk_bound_to"))
    private Player boundTo; //rp character the army is currently bound to

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, mappedBy = "army")
    private List<Unit> units = new ArrayList<>(); //the units in this army contains

    @ElementCollection
    @CollectionTable(name = "army_sieges",
                joinColumns = @JoinColumn(name = "army_id", foreignKey = @ForeignKey(name = "fk_army_id")))
    private List<String> sieges = new ArrayList<>(); //list of siege equipment this
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "stationed_at", foreignKey = @ForeignKey(name = "fk_stationed_at"))
    private ClaimBuild stationedAt; //claimbuild where this army is stationed

    @NotNull(message = "Army: freeTokens must not be null")
    private Integer freeTokens; //how many free unit tokens this army has left

    private boolean isHealing = false;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "origin_claimbuild", foreignKey = @ForeignKey(name = "fk_origin_claimbuild"))
    private ClaimBuild originalClaimbuild; //claimbuild where this army was created from

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "army", cascade = {CascadeType.REMOVE})
    private List<Movement> movements = new ArrayList<>();

    private Boolean isPaid;

    public Army(String name, ArmyType armyType, Faction faction, Region currentRegion, Player boundTo, List<Unit> units, List<String> sieges, ClaimBuild stationedAt, Integer freeTokens, boolean isHealing, ClaimBuild originalClaimbuild, LocalDateTime createdAt, boolean isPaid) {
        this.name = name;
        this.armyType = armyType;
        this.faction = faction;
        this.currentRegion = currentRegion;
        this.boundTo = boundTo;
        this.units = units;
        this.sieges = sieges;
        this.stationedAt = stationedAt;
        this.freeTokens = freeTokens;
        this.isHealing = isHealing;
        this.originalClaimbuild = originalClaimbuild;
        this.createdAt = createdAt;
        this.isPaid = isPaid;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Army army = (Army) o;
        return name.equals(army.name);
    }

    @Override
    public int hashCode() {
        return name != null ? Objects.hash(name):0;
    }
}
