package com.ardaslegends.data.domain;

import lombok.*;
import org.atmosphere.config.service.Get;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "players")
public class Player {

    @Id
    private String ign; //unique, ingame name of the player

    @Column(name = "discord_id", unique = true)
    private String discordID; //unique, the ID of the player's discord account

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction"))
    @NotNull(message = "Player: Faction must not be null")
    private Faction faction; //the faction this character belongs to

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private RPChar rpChar; //the player's rp character

}
