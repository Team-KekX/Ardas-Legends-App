package com.ardaslegends.domain.applications;

import com.ardaslegends.domain.AbstractEntity;
import com.ardaslegends.domain.Player;
import com.ardaslegends.service.exceptions.applications.RoleplayApplicationServiceException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AbstractApplication<T> extends AbstractEntity {
    private static final short REQUIRED_VOTES = 1;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "player_id")
    protected Player applicant;

    @NotNull
    @PastOrPresent
    private LocalDateTime appliedAt;

    @NotNull
    @Setter(value = AccessLevel.PROTECTED)
    private URL discordApplicationMessageLink;

    @Getter
    @NotNull
    private Short voteCount;

    @NotNull
    private LocalDateTime lastVoteAt;

    @OneToMany
    @NotNull
    private Set<Player> acceptedBy = new HashSet<>();

    @PastOrPresent
    private LocalDateTime resolvedAt;

    @Getter
    @NotNull
    @Enumerated(EnumType.STRING)
    protected ApplicationState state;

    @Setter(value = AccessLevel.PROTECTED)
    private URL discordAcceptedMessageLink;

    protected AbstractApplication(Player applicant) {
        this.applicant = applicant;
        voteCount = 0;
        appliedAt = LocalDateTime.now();
        lastVoteAt = LocalDateTime.now();
        state = ApplicationState.OPEN;

        acceptedBy = new HashSet<>(3);
    }

    protected abstract EmbedBuilder buildApplicationMessage();
    public Message sendApplicationMessage(TextChannel channel) {
        val embed = buildApplicationMessage();
        val message = channel.sendMessage(embed).join();
        this.discordApplicationMessageLink = message.getLink();
        return message;
    }
    protected abstract EmbedBuilder buildAcceptedMessage();
    public Message sendAcceptedMessage(TextChannel channel) {
        val embed = buildAcceptedMessage();
        val message = channel.sendMessage(embed).join();
        this.discordAcceptedMessageLink = message.getLink();
        return message;
    }
    public Set<Player> getAcceptedBy() {
        return Collections.unmodifiableSet(acceptedBy);
    }

    public void addAcceptor(Player player) {
        if(Boolean.FALSE.equals(player.getIsStaff())) {
            log.warn("Player [{}] cannot vote because they are not staff", player.getIgn());
            throw RoleplayApplicationServiceException.playerIsNotStaff(player.getIgn());
        }

        val success = acceptedBy.add(player);
        if(!success){
            log.warn("Player [{}] already added their vote to the application", player.getIgn());
            throw RoleplayApplicationServiceException.playerAlreadyVoted(player.getIgn());
        }
        voteCount = (short) acceptedBy.size();
        lastVoteAt = LocalDateTime.now();
    }

    public void removeAccept(Player player) {
        val success = acceptedBy.remove(player);
        if(!success) {
            log.warn("Player [{}] did not vote on the application", player.getIgn());
            throw RoleplayApplicationServiceException.playerDidNotVote(player.getIgn());
        }
        voteCount = (short) acceptedBy.size();
    }

    public boolean acceptable() {
        return voteCount >= REQUIRED_VOTES;
    }
    public T accept() {
        resolvedAt = LocalDateTime.now();
        state = ApplicationState.ACCEPTED;
        return finishApplication();
    }

    protected abstract T finishApplication();
}
