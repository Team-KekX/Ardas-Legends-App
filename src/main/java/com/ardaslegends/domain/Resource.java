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
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_name", unique = true)
    private String resourceName;

    private String minecraftItemId;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "producedResource")
    List<ProductionSite> productionSites;

}
