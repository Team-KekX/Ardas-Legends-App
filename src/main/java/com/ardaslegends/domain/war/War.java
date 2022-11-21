package com.ardaslegends.domain.war;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.atmosphere.config.service.Get;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "wars")
public class War {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
