package com.example.projet_dev.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin")
@NoArgsConstructor
public class Admin extends Utilisateur {
    // gererUtilisateurs(), gererCentres(), gererHopitaux()
    // -> implemented later in AdminService when the other entities exist
}
