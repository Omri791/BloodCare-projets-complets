package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** CentreDeDon.lancerAppelDonneurs() : demande envoyée à un donneur, qui l'accepte (en choisissant un créneau) ou la refuse. */
@Entity
@Table(name = "appel_don")
@Getter
@Setter
@NoArgsConstructor
public class AppelDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCreation;
    private String groupeSanguin;
    private String message;

    /** EN_ATTENTE, ACCEPTE, REFUSE */
    private String statut;

    @ManyToOne
    @JoinColumn(name = "centre_de_don_id")
    @JsonIgnoreProperties({"stocks", "predictions", "demandes", "rendezVous", "hibernateLazyInitializer", "handler"})
    private CentreDeDon centreDeDon;

    @ManyToOne
    @JoinColumn(name = "donneur_id")
    @JsonIgnoreProperties({"rendezVous", "hibernateLazyInitializer", "handler"})
    private Donneur donneur;

    @PrePersist
    void onCreate() {
        if (dateCreation == null) dateCreation = LocalDateTime.now();
        if (statut == null) statut = "EN_ATTENTE";
    }
}
