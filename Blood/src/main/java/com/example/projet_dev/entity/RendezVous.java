package com.example.projet_dev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
@Getter
@Setter
@NoArgsConstructor
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_rdv")
    private LocalDateTime date;

    private String statut;

    @ManyToOne
    @JoinColumn(name = "donneur_id")
    private Donneur donneur;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "don_id")
    private Don don;

    // CentreDeDon 1 --- organise --- 0..* RendezVous
    @ManyToOne
    @JoinColumn(name = "centre_de_don_id")
    private CentreDeDon centreDeDon;
}