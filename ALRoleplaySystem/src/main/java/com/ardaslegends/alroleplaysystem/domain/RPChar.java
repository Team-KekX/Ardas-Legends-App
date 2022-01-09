package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class RPChar {

    public String name; //unique, name of the character
    public Player player; //the player who this character belongs to
    public Region currentRegion; //the region the character is currently in
    public Army boundTo; //the army that is bound to this character

}
