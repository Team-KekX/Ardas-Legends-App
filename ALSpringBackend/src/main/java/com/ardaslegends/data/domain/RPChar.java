package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public class RPChar {

    @Column(unique = true)
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "current_region", foreignKey = @ForeignKey(name = "fk_current_region"))
    @NotNull(message = "RpChar: currentRegion must not be null")
    private Region currentRegion; //the region the character is currently in

    @OneToOne
    @JoinColumn(name = "bound_to", foreignKey = @ForeignKey(name = "fk_bound_to"))
    private Army boundTo; //the army that is bound to this character

}
