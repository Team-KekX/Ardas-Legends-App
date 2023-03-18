package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@ToString

@Entity
@Table(name = "roleplay_apps")
public class RoleplayApplication extends AbstractApplication {

    @ManyToOne
    @NotNull
    private Player player;
    @NotNull
    @ManyToOne
    private Faction faction;
    @NotBlank
    private String characterName;
    @NotBlank
    private String characterTitle;
    @NotBlank
    private String whyDoYouWantToBeThisCharacter;
    @NotBlank
    private String gear;
    @NotBlank
    private String linkToLore;

    public RoleplayApplication accept() {
        log.debug("Accepting application [{}]", toString());
        setAccepted();
        return this;
    }
}
