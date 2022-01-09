package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Player {

    public String ign; //unique, ingame name of the player
    public String discordID; //unique, the ID of the player's discord account
    public Faction faction; //the faction this character belongs to
    public RPChar rpChar; //the player's rp character

}
