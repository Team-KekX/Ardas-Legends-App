package com.ardaslegends.service.discord.messages.war;

import com.ardaslegends.domain.Faction;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.war.War;
import com.ardaslegends.domain.war.WarParticipant;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.FactionBanners;
import com.ardaslegends.presentation.discord.utils.Thumbnails;
import com.ardaslegends.service.discord.DiscordService;
import com.ardaslegends.service.discord.messages.ALMessage;
import lombok.val;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class WarMessages {

    public static ALMessage declareWar(War war, DiscordService discordService) {

        val attacker = war.getInitialAttacker().getWarParticipant();
        val defender = war.getInitialDefender().getWarParticipant();

        val attackerRole = discordService.getRoleByIdOrElseThrow(attacker.getFactionRoleId());
        val defenderRole = discordService.getRoleByIdOrElseThrow(defender.getFactionRoleId());

        AllowedMentions mentions = new AllowedMentionsBuilder()
                .setMentionRoles(true)
                .build();

        MessageBuilder message = new MessageBuilder()
                .setAllowedMentions(mentions)
                .append(attackerRole.getMentionTag())
                .append(" declared a War against ")
                .append(defenderRole.getMentionTag())
                .append("!");


        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(war.getName())
                .setDescription(message.getStringBuilder().toString())
                .addInlineField("Attacker", attacker.getName())
                .addInlineField("Defender", defender.getName())
                .setColor(ALColor.GREEN)
                .setThumbnail(FactionBanners.getBannerUrl(attacker.getName()))
                .setTimestampToNow();

        return new ALMessage(message, List.of(embed));
    }

    public static ALMessage forceEndWar(War war, Player warEndedByPlayer, DiscordService discordService) {
        AllowedMentions mentions = new AllowedMentionsBuilder()
                .setMentionRoles(true)
                .build();

        val warEndedByUser = discordService.getUserById(warEndedByPlayer.getDiscordID());

        val attacker = war.getInitialAttacker().getWarParticipant();
        val defender = war.getInitialDefender().getWarParticipant();

        val attackerRole = discordService.getRoleByIdOrElseThrow(attacker.getFactionRoleId());
        val defenderRole = discordService.getRoleByIdOrElseThrow(defender.getFactionRoleId());

        val message = new MessageBuilder()
                .setAllowedMentions(mentions)
                .append("The war between ")
                .append(attackerRole.getMentionTag())
                .append(" and ")
                .append(defenderRole.getMentionTag())
                .append(" was ended by ")
                .append(warEndedByUser.getMentionTag())
                .append("!");

        val embed = new EmbedBuilder()
                .setTitle(war.getName() + " was ended by staff!")
                .setColor(ALColor.YELLOW)
                .setDescription("The attacking war of %s against %s was ended by staff member %s!".formatted(attackerRole.getMentionTag(), defenderRole.getMentionTag(), warEndedByUser.getMentionTag()))
                .addInlineField("Attackers", war.getAggressors().stream().map(WarParticipant::getName).collect(Collectors.joining("\n")))
                .addInlineField("Defenders", war.getDefenders().stream().map(WarParticipant::getName).collect(Collectors.joining("\n")))
                .addField("War ended by", warEndedByUser.getMentionTag())
                .setThumbnail(Thumbnails.END_WAR.getUrl())
                .setTimestampToNow();

        return new ALMessage(message, List.of(embed));
    }
}
