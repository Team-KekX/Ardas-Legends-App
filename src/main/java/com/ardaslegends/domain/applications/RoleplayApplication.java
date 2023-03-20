package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.DiscordUtils;
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
public class RoleplayApplication extends AbstractApplication implements DiscordUtils {

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

    @Override
    public EmbedBuilder buildApplicationMessage() {
        return new EmbedBuilder()
                .setTitle(player.getIgn() + "'s Application")
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
                .setTitle("Accepted: " + player.getIgn() + "'s Character")
                .addField("Character", characterName)
                .addField("Title", characterTitle)
                .addField("Faction", faction.getName())
                .addField("Gear", gear)
                .addField("Link to RP", linkToLore)
                .setColor(ALColor.GREEN)
                .setThumbnail(getFactionBanner(faction.getName()))
                .setTimestampToNow();
    }
}
