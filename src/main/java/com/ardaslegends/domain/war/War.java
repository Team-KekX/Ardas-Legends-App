package com.ardaslegends.domain.war;

import com.ardaslegends.domain.AbstractDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "wars")
public class War extends AbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ElementCollection
    private List<WarParticipant> aggressors;

    @ElementCollection
    private List<WarParticipant> defenders;


    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @OneToMany(mappedBy = "war")
    private List<Battle> battles;

}
