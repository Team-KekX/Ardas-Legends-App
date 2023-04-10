package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.RPChar;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.DiscordUtils;
import com.ardaslegends.service.exceptions.applications.RoleplayApplicationServiceException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;

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
public class RoleplayApplication extends AbstractApplication<RPChar> implements DiscordUtils {

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
    @NotNull
    private Boolean pvp;
    @NotBlank
    private String linkToLore;

    public RoleplayApplication(Player applicant, Faction faction, String characterName, String characterTitle, String whyDoYouWantToBeThisCharacter, String gear, Boolean pvp, String linkToLore) {
        super(applicant);
        this.faction = faction;
        this.characterName = characterName;
        this.characterTitle = characterTitle;
        this.whyDoYouWantToBeThisCharacter = whyDoYouWantToBeThisCharacter;
        this.gear = gear;
        this.pvp = pvp;
        this.linkToLore = linkToLore;
    }

    @Override
    public EmbedBuilder buildApplicationMessage() {
        return new EmbedBuilder()
                .setTitle("Roleplay Character Application")
                .addField("Applicant", applicant.getIgn())
                .addField("Character", characterName)
                .addField("Title", characterTitle)
                .addField("Faction", faction.getName())
                .addField("Gear", gear)
                .addField("Link to RP", linkToLore)
                .setColor(ALColor.YELLOW)
                .setThumbnail(getFactionBanner(faction.getName()))
                .setTimestampToNow();
    }

    @Override
    public EmbedBuilder buildAcceptedMessage() {
        return new EmbedBuilder()
                .setTitle("Accepted: " + applicant.getIgn() + "'s Character")
                .addField("Character", characterName)
                .addInlineField("Title", characterTitle)
                .addField("Faction", faction.getName())
                .addInlineField("Gear", gear)
                .addField("Link to RP", linkToLore)
                .setColor(ALColor.GREEN)
                .setThumbnail(getFactionBanner(faction.getName()))
                .setTimestampToNow();
    }

    @Override
    protected RPChar finishApplication() {
        if(state != ApplicationState.ACCEPTED) {
            throw RoleplayApplicationServiceException.applicationNotYetAccepted(getId());
        }
        return new RPChar(this);
    }
}
