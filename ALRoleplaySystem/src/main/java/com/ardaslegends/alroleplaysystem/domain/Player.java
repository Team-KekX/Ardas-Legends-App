package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Player {

    public String ign;
    public String discordID;
    public Faction faction;
    public RPChar rpChar;

}
