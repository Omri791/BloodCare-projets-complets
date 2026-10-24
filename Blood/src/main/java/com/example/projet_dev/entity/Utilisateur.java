package com.example.projet_dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    // never sent back to Angular
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String motDePasse;

    @JsonIgnore   //évite la boucle JSON
    @OneToMany(mappedBy = "utilisateur",cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    /** L'authentification est faite par Keycloak : la colonne est conservée mais n'est plus utilisée. */
    @PrePersist
    void ensurePasswordPlaceholder() {
        if (motDePasse == null || motDePasse.isBlank()) {
            motDePasse = "KEYCLOAK-" + UUID.randomUUID();
        }
    }

    /** Type de compte exposé au front : ADMIN, HOPITAL, CENTRE_DON, DONNEUR. */
    @JsonProperty(value = "role", access = JsonProperty.Access.READ_ONLY)
    public String getRole() {
        return switch (Hibernate.getClass(this).getSimpleName()) {
            case "Admin" -> "ADMIN";
            case "Hopital" -> "HOPITAL";
            case "CentreDeDon" -> "CENTRE_DON";
            case "Donneur" -> "DONNEUR";
            default -> "UTILISATEUR";
        };
    }
}
