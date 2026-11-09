package com.example.projet_dev.service;

import com.example.projet_dev.entity.Creneau;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.repository.CreneauRepository;
import com.example.projet_dev.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreneauService {

    /** Rendez-vous qui occupent une place sur un créneau */
    private static final Set<String> OCCUPE = Set.of("PLANIFIE", "CONFIRME", "TERMINE");

    private final CreneauRepository repository;
    private final RendezVousRepository rendezVousRepository;
    private final CentreDeDonService centreService;

    /** Tous les créneaux du centre, avec le nombre de places restantes */
    public List<Creneau> findByCentre(Long centreId) {
        List<RendezVous> rdvs = rendezVousRepository.findByCentreDeDonId(centreId);
        List<Creneau> creneaux = repository.findByCentreDeDonIdOrderByDateAsc(centreId);
        creneaux.forEach(c -> c.setPlacesRestantes(c.getCapacite() - occupees(c, rdvs)));
        return creneaux;
    }

    /** Créneaux futurs encore libres : ceux que le donneur peut choisir */
    public List<Creneau> findDisponibles(Long centreId) {
        LocalDateTime now = LocalDateTime.now();
        return findByCentre(centreId).stream()
                .filter(c -> c.getDate().isAfter(now) && c.getPlacesRestantes() > 0)
                .toList();
    }

    public Creneau findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Créneau introuvable"));
    }

    public Creneau create(Long centreId, LocalDateTime date, Integer capacite) {
        if (date == null || date.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date du créneau doit être dans le futur");
        }
        if (capacite == null || capacite < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La capacité doit être d'au moins 1 donneur");
        }
        boolean existe = repository.findByCentreDeDonIdOrderByDateAsc(centreId).stream()
                .anyMatch(c -> c.getDate().equals(date));
        if (existe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un créneau existe déjà à cette date et heure");
        }
        Creneau c = new Creneau();
        c.setDate(date);
        c.setCapacite(capacite);
        c.setCentreDeDon(centreService.findById(centreId));
        Creneau saved = repository.save(c);
        saved.setPlacesRestantes(capacite);
        return saved;
    }

    public void delete(Long centreId, Long id) {
        Creneau c = findById(id);
        if (!c.getCentreDeDon().getId().equals(centreId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Créneau introuvable");
        }
        if (occupees(c, rendezVousRepository.findByCentreDeDonId(centreId)) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Des donneurs ont déjà réservé ce créneau : annulez d'abord leurs rendez-vous");
        }
        repository.delete(c);
    }

    /** Places restantes d'un créneau (utilisé au moment de la réservation) */
    public int placesRestantes(Creneau c) {
        return c.getCapacite() - occupees(c, rendezVousRepository.findByCentreDeDonId(c.getCentreDeDon().getId()));
    }

    private static int occupees(Creneau c, List<RendezVous> rdvs) {
        return (int) rdvs.stream()
                .filter(r -> c.getDate().equals(r.getDate()) && OCCUPE.contains(r.getStatut()))
                .count();
    }
}
