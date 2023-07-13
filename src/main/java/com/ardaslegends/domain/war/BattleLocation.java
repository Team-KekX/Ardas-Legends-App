package com.ardaslegends.domain.war;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.Region;

import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class BattleLocation {

    @ManyToOne
    @JoinColumn(name = "region_id", foreignKey = @ForeignKey(name = "fk_battle_location_region_id"))
    private Region region;

    private Boolean fieldBattle;

    @ManyToOne
    @JoinColumn(name = "claimbuild_id", foreignKey = @ForeignKey(name = "fk_battle_location_claimbuild_id"))
    private ClaimBuild claimBuild;

}
