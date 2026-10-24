package com.example.projet_dev.service;

import com.example.projet_dev.entity.Notification;
import com.example.projet_dev.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final UtilisateurService utilisateurService;

    public List<Notification> findAll() {
        return repository.findAll();
    }

    public List<Notification> findByUtilisateur(Long utilisateurId) {
        return repository.findByUtilisateurId(utilisateurId);
    }

    public Notification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification introuvable"));
    }

    /** Notification.envoyer() */
    public Notification envoyer(Long utilisateurId, Notification notification) {
        notification.setId(null);
        notification.setDate(LocalDate.now());
        notification.setLu(false);
        notification.setUtilisateur(utilisateurService.findById(utilisateurId));
        return repository.save(notification);
    }

    /** Notification.marquerCommeLue() */
    public Notification marquerCommeLue(Long id) {
        Notification n = findById(id);
        n.setLu(true);
        return repository.save(n);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}