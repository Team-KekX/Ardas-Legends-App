package com.ardaslegends.alroleplaysystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class RPChar {

    private final String name; //unique, name of the character
    private final Player player; //the player who this character belongs to
    private Region currentRegion; //the region the character is currently in
    private Army boundTo; //the army that is bound to this character

    public void setCurrentRegion(Region currentRegion) {
        this.currentRegion = currentRegion;
    }

    public void setBoundTo(Army boundTo) {
        this.boundTo = boundTo;
    }
}
