package com.ardaslegends.data.domain;

import com.ardaslegends.data.service.utils.ServiceUtils;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
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

    @ElementCollection
    private List<PathElement> path;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Boolean isCurrentlyActive;
    private Integer hoursUntilComplete;
    private Integer hoursMoved;
    private Integer hoursUntilNextRegion;

    public String getStartRegionId() { return path.get(0).getRegion().getId(); }
    public String getDestinationRegionId() { return path.get(path.size()-1).getRegion().getId(); }

    public Movement(Player player, Army army, Boolean isCharMovement, List<PathElement> path, LocalDateTime startTime, LocalDateTime endTime, Boolean isCurrentlyActive, Integer hoursUntilComplete, Integer hoursUntilNextRegion, Integer hoursMoved) {
        this.player = player;
        this.army = army;
        this.isCharMovement = isCharMovement;
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCurrentlyActive = isCurrentlyActive;
        this.hoursUntilComplete = hoursUntilComplete;
        this.hoursUntilNextRegion = hoursUntilNextRegion;
        this.hoursMoved = hoursMoved;
    }

    public Integer getCost() {
        return ServiceUtils.getTotalPathCost(path);
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
