package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "claimbuild_apps")
public class ClaimbuildApplication extends AbstractApplication<ClaimBuild> {

    @ManyToOne
    @NotNull
    private Faction ownedBy;
    @ManyToOne
    @NotNull
    private Region region;
    @NotNull
    private ClaimBuildType claimBuildType;
    @NotNull
    private Coordinate coordinate;
    @OneToMany
    private Set<ProductionClaimbuild> productionSites;
    @ElementCollection(targetClass = SpecialBuilding.class)
    @Enumerated(EnumType.STRING)
    private List<SpecialBuilding> specialBuildings;
    private String traders;
    private String siege;
    private String numberOfHouses;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Player> builtBy;

    @Override
    protected EmbedBuilder buildApplicationMessage() {
        return null;
    }

    @Override
    protected EmbedBuilder buildAcceptedMessage() {
        return null;
    }

    @Override
    protected ClaimBuild finishApplication() {
        return null;
    }
}
