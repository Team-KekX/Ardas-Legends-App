package com.ardaslegends.domain;

import com.ardaslegends.service.utils.ServiceUtils;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Slf4j

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

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private Boolean isCurrentlyActive;

    private OffsetDateTime endsAt;
    private OffsetDateTime reachesNextRegionAt;

    public String getStartRegionId() { return path.get(0).getRegion().getId(); }
    public String getDestinationRegionId() { return path.get(path.size()-1).getRegion().getId(); }

    public Movement(RPChar rpChar, Army army, Boolean isCharMovement, List<PathElement> path, OffsetDateTime startTime, OffsetDateTime endTime, Boolean isCurrentlyActive, OffsetDateTime endsAt, OffsetDateTime reachesNextRegionAt) {
        this.rpChar = rpChar;
        this.army = army;
        this.isCharMovement = isCharMovement;
        this.path = path;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCurrentlyActive = isCurrentlyActive;
        this.endsAt = endsAt;
        this.reachesNextRegionAt = reachesNextRegionAt;
    }

    public Integer getCost() {
        return ServiceUtils.getTotalPathCost(path);
    }


    public Region getCurrentRegion() {
        return isCharMovement ? rpChar.getCurrentRegion() : army.getCurrentRegion();
    }

    /**
     * Returns the next region in the path
     * @return The next region. Null if there is no next region
     */
    public Region getNextRegion() {
        val nextPathElement = getNextPathElement();
        return nextPathElement == null ? null : nextPathElement.getRegion();
    }

    public PathElement getCurrentPathElement() {
        val currentRegion = getCurrentRegion();
        return path.stream().filter(pathElement -> pathElement.hasRegion(currentRegion)).findFirst()
                .orElseThrow(() -> {
                    log.warn("COULD NOT FIND REGION [{}] IN PATH [{}] OF MOVEMENT [{}] - THIS ERROR SHOULD NEVER BE THROWN",
                            currentRegion, ServiceUtils.buildPathString(path), this);
                    return new IllegalStateException("COULD NOT FIND REGION %s IN PATH %s. PLEASE CONTACT A DEV IMMEDIATELY"
                            .formatted(currentRegion.getId(), ServiceUtils.buildPathString(path)));
                });
    }

    /**
     * Returns the next PathElement in the path
     * @return The next PathElement. Null if there is no next region
     */
    public PathElement getNextPathElement() {
        val currentPathElement = getCurrentPathElement();
        val nextRegionIndex = path.indexOf(currentPathElement) + 1;
        if(nextRegionIndex >= path.size())
            return null;
        return path.get(nextRegionIndex);
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
