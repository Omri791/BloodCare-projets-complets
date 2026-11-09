package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Créneau de collecte proposé par un centre ; le donneur en choisit un quand il accepte un appel. */
@Entity
@Table(name = "creneau")
@Getter
@Setter
@NoArgsConstructor
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_creneau")
    private LocalDateTime date;

    /** Nombre de donneurs accueillis sur ce créneau */
    private Integer capacite;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "centre_de_don_id")
    private CentreDeDon centreDeDon;

    /** Calculé par le service (capacité - rendez-vous déjà pris), non stocké */
    @Transient
    private Integer placesRestantes;
}
