package com.ardaslegends.domain;

import com.ardaslegends.service.exceptions.PlayerServiceException;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

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
public final class Player extends AbstractDomainObject {

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
    @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_player_faction"))
    @NotNull(message = "Player: Faction must not be null")
    private Faction faction; //the faction this character belongs to

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "character_id", foreignKey = @ForeignKey(name = "fk_player_character_id"))
    private RPChar rpChar; //the player's rp character

    @OneToMany
    @JoinColumn(name = "past_char_id", foreignKey = @ForeignKey(name = "fk_player_past_char_id"))
    @Setter(AccessLevel.NONE)
    private Set<RPChar> pastCharacters;

    @ManyToMany(mappedBy = "builtBy", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<ClaimBuild> builtClaimbuilds;

    private Boolean isStaff;

    public Player(String ign, String uuid, String discordID, Faction faction, RPChar rpChar) {
        this.ign = ign;
        this.uuid = uuid;
        this.discordID = discordID;
        this.faction = faction;
        this.rpChar = rpChar;
        this.builtClaimbuilds = new ArrayList<>(1);
        this.isStaff = false;
    }

    @JsonIgnore
    public void hasRpCharThrowExceptionOnFalse() {
        log.debug("Checking if player [{}] has an rpchar and throwing exception on false", this.getIgn());

        if(rpChar == null) {
            log.warn("Player [{}] has no rpchar", this.getIgn());
            throw PlayerServiceException.playerHasNoRpchar();
        }
    }

    public void setRpChar(RPChar rpChar, Faction faction) {
        this.rpChar = rpChar;
    }

    public Set<RPChar> getPastCharacters() {
        return Collections.unmodifiableSet(pastCharacters);
    }

    public List<ClaimBuild> getBuiltClaimbuilds() {
        return Collections.unmodifiableList(builtClaimbuilds);
    }

    @Override
    public String toString() {
        return ign;
    }
}
