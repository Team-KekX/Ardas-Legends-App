package com.ardaslegends.domain.war;

import com.ardaslegends.domain.ClaimBuild;
import com.ardaslegends.domain.Region;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class BattleLocation {

    @ManyToOne
    private Region region;

    private Boolean fieldBattle;

    @ManyToOne
    private ClaimBuild claimBuild;

}
