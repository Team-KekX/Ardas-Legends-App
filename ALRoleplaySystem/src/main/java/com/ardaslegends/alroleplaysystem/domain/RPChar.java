package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class RPChar {

    private String name; //unique, name of the character
    private Player player; //the player who this character belongs to
    private Region currentRegion; //the region the character is currently in
    private Army boundTo; //the army that is bound to this character

}
