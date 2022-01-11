package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Player {

    private String ign; //unique, ingame name of the player
    private String discordID; //unique, the ID of the player's discord account
    private Faction faction; //the faction this character belongs to
    private RPChar rpChar; //the player's rp character

}
