package com.ardaslegends.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
@Table(name = "movements")
public final class Movement extends AbstractDomainEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "army_name")
    private Army army; //Is null when it's a Rp Char movement

    private Boolean isCharMovement; //Should be true when army = null

    @Embedded
    private Path path;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Boolean isCurrentlyActive;
    private Integer hoursUntilComplete;
    private Integer hoursMoved;
    private Integer hoursUntilNextRegion;

    public String getStartRegionId() { return path.getStart(); }
    public String getDestinationRegionId() { return path.getDestination(); }

    public Movement(Player player, Army army, Boolean isCharMovement, Path path, LocalDateTime startTime, LocalDateTime endTime, Boolean isAccepted, Boolean isCurrentlyActive, Integer hoursUntilComplete, Integer hoursUntilNextRegion, Integer hoursMoved) {
        this.player = player;
        this.army = army;
        this.isCharMovement = isCharMovement;
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAccepted = isAccepted;
        this.isCurrentlyActive = isCurrentlyActive;
        this.hoursUntilComplete = hoursUntilComplete;
        this.hoursUntilNextRegion = hoursUntilNextRegion;
        this.hoursMoved = hoursMoved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return Objects.equals(id, movement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movement{" +
                "player=" + player +
                ", army=" + army +
                ", isCharMovement=" + isCharMovement +
                ", path=" + path +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
