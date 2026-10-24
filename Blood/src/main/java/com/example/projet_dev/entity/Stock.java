package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupeSanguin;
    private Integer quantiteDisponible;
    private LocalDate derniereMaj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centre_de_don_id")
    @JsonIgnoreProperties({"stocks", "predictions", "demandes", "hibernateLazyInitializer", "handler"})
    private CentreDeDon centreDeDon;

    @PrePersist
    @PreUpdate
    void onSave() {
        derniereMaj = LocalDate.now();
        if (quantiteDisponible == null) quantiteDisponible = 0;
    }
}
