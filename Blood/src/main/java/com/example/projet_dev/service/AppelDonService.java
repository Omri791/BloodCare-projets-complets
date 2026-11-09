package com.example.projet_dev.service;

import com.example.projet_dev.entity.AppelDon;
import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.Creneau;
import com.example.projet_dev.entity.Donneur;
import com.example.projet_dev.entity.Notification;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.repository.AppelDonRepository;
import com.example.projet_dev.repository.DonneurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppelDonService {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

    private final AppelDonRepository repository;
    private final DonneurRepository donneurRepository;
    private final CentreDeDonService centreService;
    private final CreneauService creneauService;
    private final RendezVousService rendezVousService;
    private final NotificationService notificationService;

    /**
     * CentreDeDon.lancerAppelDonneurs()
     *  - donneurIds renseigné : appel envoyé à ces donneurs
     *  - sinon : à tous les donneurs disponibles (du groupe sanguin demandé s'il est précisé)
     * Un donneur qui a déjà un appel en attente de ce centre n'en reçoit pas un deuxième.
     * @return le nombre de donneurs appelés
     */
    @Transactional
    public int lancer(Long centreId, String groupeSanguin, String message, List<Long> donneurIds) {
        CentreDeDon centre = centreService.findById(centreId);
        if (creneauService.findDisponibles(centreId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ajoutez d'abord des créneaux disponibles : le donneur devra en choisir un");
        }
        String groupe = groupeSanguin == null || groupeSanguin.isBlank() ? null : groupeSanguin;

        List<Donneur> cibles = donneurIds != null && !donneurIds.isEmpty()
                ? donneurRepository.findAllById(donneurIds)
                : donneurRepository.findByDisponibiliteTrue().stream()
                    .filter(d -> groupe == null || groupe.equals(d.getGroupeSanguin()))
                    .toList();

        int count = 0;
        for (Donneur d : cibles) {
            if (repository.existsByCentreDeDonIdAndDonneurIdAndStatut(centreId, d.getId(), "EN_ATTENTE")) continue;
            AppelDon a = new AppelDon();
            a.setCentreDeDon(centre);
            a.setDonneur(d);
            a.setGroupeSanguin(groupe);
            a.setMessage(message == null || message.isBlank()
                    ? "Le centre " + centre.getNom() + " a besoin de donneurs" + (groupe == null ? "" : " " + groupe) + "."
                    : message);
            repository.save(a);

            Notification n = new Notification();
            n.setType("APPEL");
            n.setMessage("Appel au don du centre " + centre.getNom() + " : choisissez un créneau dans votre espace.");
            notificationService.envoyer(d.getId(), n);
            count++;
        }
        if (count == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Aucun donneur à appeler (aucun donneur disponible ou appel déjà en attente)");
        }
        return count;
    }

    public List<AppelDon> findByCentre(Long centreId) {
        return repository.findByCentreDeDonIdOrderByIdDesc(centreId);
    }

    /** Recevoir appel du centre : appels en attente du donneur */
    public List<AppelDon> findEnAttente(Long donneurId) {
        return repository.findByDonneurIdAndStatutOrderByIdDesc(donneurId, "EN_ATTENTE");
    }

    /** Créneaux libres du centre qui a lancé l'appel */
    public List<Creneau> creneauxPourAppel(Long donneurId, Long appelId) {
        AppelDon a = findForDonneur(donneurId, appelId);
        return creneauService.findDisponibles(a.getCentreDeDon().getId());
    }

    /** Le donneur accepte l'appel en choisissant un créneau : un rendez-vous confirmé est créé */
    @Transactional
    public RendezVous accepter(Long donneurId, Long appelId, Long creneauId) {
        AppelDon a = findForDonneur(donneurId, appelId);
        if (creneauId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choisissez un créneau");
        }
        Creneau c = creneauService.findById(creneauId);
        if (!c.getCentreDeDon().getId().equals(a.getCentreDeDon().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce créneau n'appartient pas au centre de l'appel");
        }
        if (creneauService.placesRestantes(c) <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce créneau est complet, choisissez-en un autre");
        }
        RendezVous rdv = rendezVousService.reserverCreneau(donneurId, c);

        a.setStatut("ACCEPTE");
        repository.save(a);

        Notification n = new Notification();
        n.setType("APPEL");
        n.setMessage(a.getDonneur().getNom() + " a accepté votre appel : rendez-vous le " + c.getDate().format(FORMAT) + ".");
        notificationService.envoyer(a.getCentreDeDon().getId(), n);
        return rdv;
    }

    public AppelDon refuser(Long donneurId, Long appelId) {
        AppelDon a = findForDonneur(donneurId, appelId);
        a.setStatut("REFUSE");
        return repository.save(a);
    }

    /** Appel en attente adressé à ce donneur (sinon 404 / 409) */
    private AppelDon findForDonneur(Long donneurId, Long appelId) {
        AppelDon a = repository.findById(appelId)
                .filter(x -> x.getDonneur().getId().equals(donneurId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appel introuvable"));
        if (!"EN_ATTENTE".equals(a.getStatut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà répondu à cet appel");
        }
        return a;
    }
}
