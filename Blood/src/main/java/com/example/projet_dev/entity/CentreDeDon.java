package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "centre_de_don")
@Getter
@Setter
@NoArgsConstructor
public class CentreDeDon extends Utilisateur {

    private String adresse;
    private String ville;
    private Float latitude;
    private Float longitude;
    private Integer capaciteStock;
    private String horaires;

    // CentreDeDon 1 --- gere --- 0..* Stock
    @JsonIgnore
    @OneToMany(mappedBy = "centreDeDon", cascade = CascadeType.ALL)
    private List<Stock> stocks = new ArrayList<>();

    // CentreDeDon 1 --- consulte --- 0..* Prediction
    @JsonIgnore
    @OneToMany(mappedBy = "centreDeDon", cascade = CascadeType.ALL)
    private List<Prediction> predictions = new ArrayList<>();

    // CentreDeDon 1 --- traite --- 0..* DemandeDeSang
    @JsonIgnore
    @OneToMany(mappedBy = "centreDeDon")
    private List<DemandeDeSang> demandes = new ArrayList<>();


    // CentreDeDon 1 --- organise --- 0..* RendezVous
    @JsonIgnore
    @OneToMany(mappedBy = "centreDeDon")
    private List<RendezVous> rendezVous = new ArrayList<>();

}
