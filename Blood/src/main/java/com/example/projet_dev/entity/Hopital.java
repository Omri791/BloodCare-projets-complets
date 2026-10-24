package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hopital")
@Getter
@Setter
@NoArgsConstructor
public class Hopital extends Utilisateur {

    private String adresse;
    private String ville;
    private Float latitude;
    private Float longitude;
    private String type;
    private Integer capaciteLits;

    // Hopital 1 --- gere --- 0..* Patient
    @JsonIgnore
    @OneToMany(mappedBy = "hopital", cascade = CascadeType.ALL)
    private List<Patient> patients = new ArrayList<>();

    // Hopital 1 --- envoie --- 0..* DemandeDeSang
    @JsonIgnore
    @OneToMany(mappedBy = "hopital", cascade = CascadeType.ALL)
    private List<DemandeDeSang> demandes = new ArrayList<>();
}
