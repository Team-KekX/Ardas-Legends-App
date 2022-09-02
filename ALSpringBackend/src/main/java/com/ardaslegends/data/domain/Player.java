package com.ardaslegends.data.domain;

import com.ardaslegends.data.service.exceptions.FactionServiceException;
import com.ardaslegends.data.service.exceptions.PlayerServiceException;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Slf4j
@Entity
@Table(name = "players")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "ign")
public final class Player extends AbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "Player: IGN must not be null")
    private String ign; //unique, ingame name of the player

    @Column(unique = true)
    @NotNull(message = "Player: UUID must not be null")
    private String uuid;

    @Column(name = "discord_id", unique = true)
    @NotNull(message = "Player: DiscordID must not be null")
    private String discordID; //unique, the ID of the player's discord account

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction"))
    @NotNull(message = "Player: Faction must not be null")
    private Faction faction; //the faction this character belongs to

    @Embedded
    private RPChar rpChar; //the player's rp character

    @JsonIgnore
    @OneToMany(mappedBy = "player", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    public List<Movement> movements = new ArrayList<>();

    @JsonIgnore
    public void hasRpCharThrowExceptionOnFalse() {
        log.debug("Checking if player [{}] has an rpchar and throwing exception on false", this.getIgn());

        if(rpChar == null) {
            log.warn("Player [{}] has no rpchar", this.getIgn());
            throw PlayerServiceException.playerHasNoRpchar();
        }
    }
    @Override
    public String toString() {
        return ign;
    }
}
