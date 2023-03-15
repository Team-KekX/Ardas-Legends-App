package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j

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
}
