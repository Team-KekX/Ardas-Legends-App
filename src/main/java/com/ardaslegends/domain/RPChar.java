package com.ardaslegends.domain;

import com.ardaslegends.domain.applications.RoleplayApplication;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
public class RPChar extends AbstractEntity {

    @ManyToOne
    private Player owner;

    @Column(unique = true)
    private String name;

    @Length(max = 25, message = "Title too long!")
    private String title;

    private String gear;

    private Boolean pvp;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "current_region", foreignKey = @ForeignKey(name = "fk_current_region"))
    @NotNull(message = "RpChar: currentRegion must not be null")
    private Region currentRegion; //the region the character is currently in

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "bound_to", foreignKey = @ForeignKey(name = "fk_bound_to"))
    private Army boundTo; //the army that is bound to this character

    @OneToMany(mappedBy = "rpChar")
    private Set<Movement> movements;

    private Boolean injured;
    private Boolean isHealing;
    private LocalDateTime startedHeal;
    private LocalDateTime healEnds;
    private String linkToLore;

    private Boolean deleted;

    public RPChar(RoleplayApplication application) {
        name = application.getCharacterName();
        title = application.getCharacterTitle();
        gear = application.getGear();
        pvp = application.getPvp();

        currentRegion = application.getFaction().getHomeRegion();
        movements = new HashSet<>();

        boundTo = null;
        injured = false;
        isHealing = false;
        startedHeal = null;
        healEnds = null;
        deleted = false;

        linkToLore = application.getLinkToLore();
    }

    public RPChar(Player owner, String name, String title, String gear, Boolean pvp, String linkToLore) {
        this.owner = owner;
        this.name = name;
        this.title = title;
        this.gear = gear;
        this.pvp = pvp;

        this.currentRegion = owner.getFaction().getHomeRegion();
        this.movements = new HashSet<>();

        boundTo = null;
        injured = false;
        isHealing = false;
        startedHeal = null;
        healEnds = null;
        deleted = false;

        this.linkToLore = linkToLore;
    }

    public Set<Movement> getMovements() {
        return Collections.unmodifiableSet(movements);
    }

    @Override
    public String toString() {
        return name;
    }
}
