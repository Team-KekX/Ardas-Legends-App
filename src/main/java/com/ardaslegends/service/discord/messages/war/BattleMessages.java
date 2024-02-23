package com.ardaslegends.service.discord.messages.war;

import com.ardaslegends.domain.Army;
import com.ardaslegends.domain.Player;
import com.ardaslegends.domain.RPChar;
import com.ardaslegends.domain.war.battle.Battle;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.presentation.discord.utils.Thumbnails;
import com.ardaslegends.service.discord.DiscordService;
import com.ardaslegends.service.discord.messages.ALMessage;
import lombok.val;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class BattleMessages {

    public static ALMessage declareBattle(Battle battle, DiscordService discordService) {
        Objects.requireNonNull(battle, "Declare Battle discord message got passed null value for battle");
        Objects.requireNonNull(battle, "Declare Battle discord message got passed null value for discordService");

        AllowedMentions mentions = new AllowedMentionsBuilder()
                .setMentionUsers(true)
                .build();

        val attackingArmy = battle.getInitialAttacker();
        val defendingArmies = battle.getDefendingArmies();
        val isFieldBattle = battle.getBattleLocation().getFieldBattle();

        MessageBuilder message = new MessageBuilder()
                .setAllowedMentions(mentions)
                .append("The army ")
                .append(attackingArmy.getName())
                .append(" has declared battle on the ")
                .append((isFieldBattle ? "army " :
                        battle.getBattleLocation().getClaimBuild().getOwnedBy().getName() + " " +
                        battle.getBattleLocation().getClaimBuild().getType().getName() + " "))
                .append((isFieldBattle ? battle.getFirstDefender().getName() : battle.getBattleLocation().getClaimBuild().getName()))
                .append("!");

        val embedDescription = message.getStringBuilder().toString();

        if(defendingArmies.stream().anyMatch(army -> army.getBoundTo() != null)) {
            val defendingPlayerMentions = defendingArmies.stream()
                    .map(Army::getBoundTo)
                    .filter(Objects::nonNull)
                    .map(RPChar::getOwner)
                    .map(Player::getDiscordID)
                    .map(discordService::getUserById)
                    .map(User::getMentionTag)
                    .collect(Collectors.joining(", "));

            message.appendNewLine()
                    .append("Defending players: ")
                    .append(defendingPlayerMentions);
        }

        String attackingArmyWithFactionPing = attackingArmy.getName() +
                " (" +
                discordService.getRoleByIdOrElseThrow(attackingArmy.getFaction().getFactionRoleId()).getMentionTag() +
                ")";

        String defendingArmiesWithFactionPings = defendingArmies.stream()
                .map(army -> {
                    val rolePing = discordService.getRoleByIdOrElseThrow(army.getFaction().getFactionRoleId()).getMentionTag();
                    return army.getName() + " (" + rolePing + ")";
                })
                .collect(Collectors.joining("\n"));

        val embed = new EmbedBuilder()
                .setTitle("Battle declared!")
                .setDescription(embedDescription)
                .addInlineField("Battle type", (isFieldBattle ? "Field battle" : "Claimbuild battle"))
                .addInlineField("Region", battle.getBattleLocation().getRegion().getId())
                .addField("", "")
                .addInlineField("Attacking army", attackingArmyWithFactionPing)
                .addInlineField("Defending armies", defendingArmiesWithFactionPings)
                .setColor(ALColor.GREEN)
                .setThumbnail(Thumbnails.DECLARE_BATTLE.getUrl())
                .setTimestampToNow();

        if(!isFieldBattle) {
            embed.addField("","")
                    .addInlineField("Claimbuild", battle.getBattleLocation().getClaimBuild().getName() +
                    " (" + discordService.getRoleByIdOrElseThrow(battle.getBattleLocation().getClaimBuild().getOwnedBy().getFactionRoleId()).getMentionTag()
                    + ")")
                    .addInlineField("Claimbuild Type", battle.getBattleLocation().getClaimBuild().getType().getName());
        }

        val embeds = new ArrayList<EmbedBuilder>();
        embeds.add(embed);

        return new ALMessage(message, embeds);
    }
}
