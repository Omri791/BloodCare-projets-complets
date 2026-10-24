package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "demande_de_sang")
@Getter
@Setter
@NoArgsConstructor
public class DemandeDeSang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCreation;
    private String groupeSanguin;
    private String composant;
    private Integer quantite;
    private String urgence;
    private String motif;
    private String statut;
    private Integer unitesFournies;
    private Float delaiTraitement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hopital_id")
    @JsonIgnoreProperties({"patients", "demandes", "hibernateLazyInitializer", "handler"})
    private Hopital hopital;

    // CentreDeDon 1 --- traite --- 0..* DemandeDeSang
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_de_don_id")
    @JsonIgnoreProperties({"stocks", "predictions", "demandes", "hibernateLazyInitializer", "handler"})
    private CentreDeDon centreDeDon;

    @PrePersist
    void onCreate() {
        if (dateCreation == null) dateCreation = LocalDateTime.now();
        if (statut == null) statut = "EN_ATTENTE";
        if (unitesFournies == null) unitesFournies = 0;
    }
}
