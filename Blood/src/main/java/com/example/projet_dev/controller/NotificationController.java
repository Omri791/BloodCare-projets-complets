package com.example.projet_dev.controller;

import com.example.projet_dev.entity.Notification;
import com.example.projet_dev.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public List<Notification> getAll(@RequestParam(required = false) Long utilisateurId) {
        return utilisateurId == null ? service.findAll() : service.findByUtilisateur(utilisateurId);
    }

    @GetMapping("/{id}")
    public Notification getById(@PathVariable Long id) {
        return service.findById(id);
    }

    /** Notification.envoyer(). body: {"message": "Rappel", "type": "RAPPEL_RDV"} */
    @PostMapping("/utilisateur/{utilisateurId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Notification envoyer(@PathVariable Long utilisateurId, @RequestBody Notification notification) {
        return service.envoyer(utilisateurId, notification);
    }

    /** Notification.marquerCommeLue() */
    @PatchMapping("/{id}/lue")
    public Notification marquerCommeLue(@PathVariable Long id) {
        return service.marquerCommeLue(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}