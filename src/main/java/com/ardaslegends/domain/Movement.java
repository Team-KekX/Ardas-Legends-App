package com.ardaslegends.domain;

import com.ardaslegends.service.utils.ServiceUtils;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import jakarta.persistence.*;
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
public final class Movement extends AbstractDomainObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rpchar_id", foreignKey = @ForeignKey(name = "fk_movement_rpchar_id"))
    private RPChar rpChar;

    @ManyToOne
    @JoinColumn(name = "army_name", foreignKey = @ForeignKey(name = "fk_movement_army_name"))
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

    public Movement(RPChar rpChar, Army army, Boolean isCharMovement, List<PathElement> path, LocalDateTime startTime, LocalDateTime endTime, Boolean isCurrentlyActive, Integer hoursUntilComplete, Integer hoursUntilNextRegion, Integer hoursMoved) {
        this.rpChar = rpChar;
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
                "roleplayCharacter=" + rpChar +
                ", army=" + army +
                ", isCharMovement=" + isCharMovement +
                ", path=" + path +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
