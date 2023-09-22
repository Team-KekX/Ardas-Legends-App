package com.ardaslegends.repository.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;
import lombok.val;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Set;

public class WarRepositoryImpl extends QuerydslRepositorySupport implements WarRepositoryCustom{


    public WarRepositoryImpl(){
        super(War.class);

    }

    @Override
    public War findWarByAggressorsAndDefenders(Set<WarParticipant> aggressors,Set<WarParticipant> defenders) {
        //QWar war

        return new War();
    }
}
