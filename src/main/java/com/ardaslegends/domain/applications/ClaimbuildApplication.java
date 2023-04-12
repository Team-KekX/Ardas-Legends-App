package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.*;
import com.ardaslegends.presentation.discord.utils.ALColor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.core.annotation.Order;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "claimbuild_apps")
public class ClaimbuildApplication extends AbstractApplication<ClaimBuild> {


    @NotBlank
    private String claimbuildName;

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

    @ElementCollection(targetClass = EmbeddedProductionSite.class)
    @CollectionTable(name = "claimbuild_application_production_sites")
    private Set<EmbeddedProductionSite> productionSites;

    @ElementCollection(targetClass = SpecialBuilding.class)
    @Enumerated(EnumType.STRING)
    private List<SpecialBuilding> specialBuildings;
    private String traders;
    private String siege;
    private String numberOfHouses;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Player> builtBy;


    @Override
    public EmbedBuilder buildApplicationMessage() {
        return new EmbedBuilder()
                .setTitle("Claimbuild Application")
                .addField("Name", claimbuildName)
                .addField("For Faction", ownedBy.getName())
                .addField("In Region", region.getId())
                .addField("Type", claimBuildType.getName())
                .addField("Coordinates", coordinate.toString())
                .addField("Production Sites", createProductionSiteString())
                .setColor(ALColor.YELLOW)
                .setTimestampToNow();
    }

    @Override
    public EmbedBuilder buildAcceptedMessage() {
        return null;
    }

    @Override
    public ClaimBuild finishApplication() {
        return null;
    }

    public String createProductionSiteString() {
        log.debug("ProductionSiteList Count: {}", productionSites.size());
        StringBuilder prodString = new StringBuilder();
        productionSites.forEach(productionSite -> {
            String resource = productionSite.getProductionSite().getProducedResource().getResourceName();
            String type = productionSite.getProductionSite().getType().getName();
            int count = productionSite.getCount().intValue();
            prodString.append(count).append(" ").append(resource).append(" ").append(type).append("\n");
        });

        String returnProdString = prodString.toString();
        log.debug("CreateProductionSiteString: {}", returnProdString);

        return returnProdString.isBlank() ? "None" : returnProdString;
    }

}
