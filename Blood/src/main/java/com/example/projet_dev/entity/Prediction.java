package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prediction")
@Getter
@Setter
@NoArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer mois;
    private Integer annee;
    private String groupeSanguin;
    private Integer quantitePrevue;
    private String typeAlerte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_de_don_id")
    @JsonIgnoreProperties({"stocks", "predictions", "demandes", "hibernateLazyInitializer", "handler"})
    private CentreDeDon centreDeDon;
}
