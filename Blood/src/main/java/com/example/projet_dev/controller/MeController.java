package com.example.projet_dev.controller;

import com.example.projet_dev.entity.Notification;
import com.example.projet_dev.entity.Utilisateur;
import com.example.projet_dev.service.AccessService;
import com.example.projet_dev.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Profil de l'utilisateur connecté (identifié par l'email du token Keycloak). */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final AccessService access;
    private final NotificationService notificationService;

    /** Renvoie la fiche (id, nom, email, role...) : le front en déduit l'id de l'hôpital. */
    @GetMapping
    public Utilisateur me() {
        return access.currentUser();
    }

    @GetMapping("/notifications")
    public List<Notification> notifications() {
        return notificationService.findByUtilisateur(access.currentUser().getId());
    }
}
