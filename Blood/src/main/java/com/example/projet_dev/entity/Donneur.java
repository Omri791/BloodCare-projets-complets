package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donneur")
@Getter
@Setter
@NoArgsConstructor
public class Donneur extends Utilisateur {

    private LocalDate dateNaissance;
    private String sexe;
    private String groupeSanguin;
    private String telephone;
    private String ville;
    private Float latitude;
    private Float longitude;
    private Float poids;
    private Float hemoglobine;
    private Boolean disponibilite;
    private LocalDate dernierDon;
    private Integer totalDons;

    @JsonIgnore
    @OneToMany(mappedBy = "donneur", cascade = CascadeType.ALL)
    private List<RendezVous> rendezVous = new ArrayList<>();
}