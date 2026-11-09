package com.example.projet_dev.service;

import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.Creneau;
import com.example.projet_dev.entity.Don;
import com.example.projet_dev.entity.Donneur;
import com.example.projet_dev.entity.Notification;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.entity.Stock;
import com.example.projet_dev.entity.Utilisateur;
import com.example.projet_dev.repository.DonneurRepository;
import com.example.projet_dev.repository.RendezVousRepository;
import com.example.projet_dev.repository.StockRepository;
import com.example.projet_dev.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RendezVousService {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

    private final RendezVousRepository repository;
    private final DonneurService donneurService;
    private final DonneurRepository donneurRepository;
    private final CentreDeDonService centreService;
    private final UtilisateurRepository utilisateurRepository;
    private final StockRepository stockRepository;
    private final NotificationService notificationService;

    public List<RendezVous> findAll() {
        return repository.findAll();
    }

    public List<RendezVous> findByStatut(String statut) {
        return repository.findByStatut(statut);
    }

    public List<RendezVous> findByDonneur(Long donneurId) {
        return repository.findByDonneurId(donneurId);
    }

    /** CentreDeDon organise RendezVous */
    public List<RendezVous> findByCentre(Long centreId) {
        return repository.findByCentreDeDonId(centreId);
    }

    public RendezVous findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rendez-vous introuvable"));
    }

    /** Donneur.prendreRendezVous() — body: {"date": "2026-11-20T09:30", "centreDeDon": {"id": 1}} */
    public RendezVous prendre(Long donneurId, RendezVous rdv) {
        String raison = donneurService.raisonNonEligible(donneurId);
        if (raison != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous n'êtes pas éligible au don : " + raison);
        }
        if (rdv.getDate() == null || rdv.getDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date du rendez-vous doit être dans le futur");
        }
        if (rdv.getCentreDeDon() == null || rdv.getCentreDeDon().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choisissez un centre de don");
        }
        boolean dejaUnRdv = repository.findByDonneurId(donneurId).stream()
                .anyMatch(r -> "PLANIFIE".equals(r.getStatut()) || "CONFIRME".equals(r.getStatut()));
        if (dejaUnRdv) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà un rendez-vous en cours");
        }
        rdv.setId(null);
        rdv.setDon(null);
        rdv.setStatut("PLANIFIE");
        rdv.setDonneur(donneurService.findById(donneurId));
        rdv.setCentreDeDon(centreService.findById(rdv.getCentreDeDon().getId()));
        RendezVous saved = repository.save(rdv);

        Notification n = new Notification();
        n.setType("RENDEZ_VOUS");
        n.setMessage("Nouveau rendez-vous de " + saved.getDonneur().getNom() + " le " + saved.getDate().format(FORMAT) + " (à confirmer).");
        notificationService.envoyer(saved.getCentreDeDon().getId(), n);
        return saved;
    }

    /** Le donneur réserve un créneau proposé par le centre (après un appel) : rendez-vous directement confirmé */
    public RendezVous reserverCreneau(Long donneurId, Creneau creneau) {
        String raison = donneurService.raisonNonEligible(donneurId);
        if (raison != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous n'êtes pas éligible au don : " + raison);
        }
        if (creneau.getDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce créneau est déjà passé");
        }
        boolean dejaUnRdv = repository.findByDonneurId(donneurId).stream()
                .anyMatch(r -> "PLANIFIE".equals(r.getStatut()) || "CONFIRME".equals(r.getStatut()));
        if (dejaUnRdv) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà un rendez-vous en cours");
        }
        RendezVous r = new RendezVous();
        r.setDate(creneau.getDate());
        r.setStatut("CONFIRME");
        r.setDonneur(donneurService.findById(donneurId));
        r.setCentreDeDon(creneau.getCentreDeDon());
        return repository.save(r);
    }

    /** Le donneur annule un de ses rendez-vous à venir */
    public RendezVous annulerParDonneur(Long donneurId, Long id) {
        RendezVous r = findById(id);
        if (r.getDonneur() == null || !r.getDonneur().getId().equals(donneurId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rendez-vous introuvable");
        }
        if (!"PLANIFIE".equals(r.getStatut()) && !"CONFIRME".equals(r.getStatut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce rendez-vous ne peut plus être annulé");
        }
        r.setStatut("ANNULE");
        RendezVous saved = repository.save(r);
        if (saved.getCentreDeDon() != null) {
            Notification n = new Notification();
            n.setType("RENDEZ_VOUS");
            n.setMessage(saved.getDonneur().getNom() + " a annulé son rendez-vous du " + saved.getDate().format(FORMAT) + ".");
            notificationService.envoyer(saved.getCentreDeDon().getId(), n);
        }
        return saved;
    }

    /** CentreDeDon.organiserDon() : le centre planifie un rendez-vous pour un donneur (identifié par son email) */
    public RendezVous organiser(Long centreId, String donneurEmail, LocalDateTime date) {
        if (donneurEmail == null || donneurEmail.isBlank() || date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'email du donneur et la date sont obligatoires");
        }
        if (date.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date du rendez-vous doit être dans le futur");
        }
        Utilisateur u = utilisateurRepository.findByEmailIgnoreCase(donneurEmail.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aucun donneur avec cet email (il doit s'être connecté au moins une fois)"));
        if (!(u instanceof Donneur donneur)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce compte n'est pas un donneur");
        }
        CentreDeDon centre = centreService.findById(centreId);

        RendezVous r = new RendezVous();
        r.setDate(date);
        r.setStatut("CONFIRME");
        r.setDonneur(donneur);
        r.setCentreDeDon(centre);
        RendezVous saved = repository.save(r);
        notifierDonneur(saved, "Rendez-vous de don le " + date.format(FORMAT) + " au centre " + centre.getNom() + ".");
        return saved;
    }

    /** CentreDeDon gère RendezVous : CONFIRME, ANNULE ou ABSENT */
    public RendezVous changerStatutParCentre(Long centreId, Long id, String statut) {
        if (!Set.of("CONFIRME", "ANNULE", "ABSENT").contains(statut)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide (CONFIRME, ANNULE ou ABSENT)");
        }
        RendezVous r = findForCentre(centreId, id);
        if ("TERMINE".equals(r.getStatut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce rendez-vous est déjà terminé (don enregistré)");
        }
        r.setStatut(statut);
        RendezVous saved = repository.save(r);
        if ("CONFIRME".equals(statut)) {
            notifierDonneur(saved, "Votre rendez-vous du " + saved.getDate().format(FORMAT) + " est confirmé.");
        } else if ("ANNULE".equals(statut)) {
            notifierDonneur(saved, "Votre rendez-vous du " + saved.getDate().format(FORMAT) + " a été annulé par le centre.");
        }
        return saved;
    }

    /**
     * Le centre enregistre le don d'un de ses rendez-vous.
     * groupeSanguin n'est utilisé que si le groupe du donneur n'est pas encore connu.
     */
    @Transactional
    public RendezVous enregistrerDonParCentre(Long centreId, Long id, Don don, String groupeSanguin) {
        RendezVous r = findForCentre(centreId, id);
        if ("ANNULE".equals(r.getStatut()) || "ABSENT".equals(r.getStatut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Impossible d'enregistrer un don sur un rendez-vous annulé ou absent");
        }
        if (don.getDateDon() == null) don.setDateDon(LocalDate.now());
        if (don.getDateDon().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date du don ne peut pas être dans le futur");
        }
        if (don.getQuantite() == null || don.getQuantite() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La quantité doit être positive");
        }
        Donneur d = r.getDonneur();
        if (d.getGroupeSanguin() == null) {
            if (groupeSanguin == null || groupeSanguin.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Groupe sanguin du donneur inconnu : il faut le renseigner");
            }
            d.setGroupeSanguin(groupeSanguin);
        }
        return enregistrerDon(id, don);
    }

    public RendezVous update(Long id, RendezVous data) {
        RendezVous r = findById(id);
        r.setDate(data.getDate());
        r.setStatut(data.getStatut());
        return repository.save(r);
    }

    public RendezVous changerStatut(Long id, String statut) {
        RendezVous r = findById(id);
        r.setStatut(statut);
        return repository.save(r);
    }

    /** RendezVous aboutit à Don : met à jour l'historique du donneur et ajoute une poche au stock du centre */
    @Transactional
    public RendezVous enregistrerDon(Long id, Don don) {
        RendezVous r = findById(id);
        if (r.getDon() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un don existe déjà pour ce rendez-vous");
        }
        don.setId(null);
        r.setDon(don);
        r.setStatut("TERMINE");

        Donneur d = r.getDonneur();
        d.setDernierDon(don.getDateDon());
        d.setTotalDons((d.getTotalDons() == null ? 0 : d.getTotalDons()) + 1);
        donneurRepository.save(d);

        if (r.getCentreDeDon() != null && d.getGroupeSanguin() != null) {
            ajouterAuStock(r.getCentreDeDon(), d.getGroupeSanguin());
        }
        return repository.save(r);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    // ------------------------------------------------------------------

    /** Rendez-vous de ce centre uniquement (sinon 404) */
    private RendezVous findForCentre(Long centreId, Long id) {
        RendezVous r = findById(id);
        if (r.getCentreDeDon() == null || !r.getCentreDeDon().getId().equals(centreId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rendez-vous introuvable");
        }
        return r;
    }

    /** Un don = une poche de plus dans le stock du centre pour ce groupe sanguin */
    private void ajouterAuStock(CentreDeDon centre, String groupeSanguin) {
        Stock stock = stockRepository.findByCentreDeDonId(centre.getId()).stream()
                .filter(s -> Objects.equals(s.getGroupeSanguin(), groupeSanguin))
                .findFirst()
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setGroupeSanguin(groupeSanguin);
                    s.setQuantiteDisponible(0);
                    s.setCentreDeDon(centre);
                    return s;
                });
        stock.setQuantiteDisponible(stock.getQuantiteDisponible() + 1);
        stockRepository.save(stock);
    }

    private void notifierDonneur(RendezVous r, String message) {
        if (r.getDonneur() == null) return;
        Notification n = new Notification();
        n.setType("RENDEZ_VOUS");
        n.setMessage(message);
        notificationService.envoyer(r.getDonneur().getId(), n);
    }
}
