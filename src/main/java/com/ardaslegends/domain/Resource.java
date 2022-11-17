package com.ardaslegends.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String minecraftItemId;

    private ResourceType resourceType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "producedResource")
    List<ProductionSite> productionSites;

}
