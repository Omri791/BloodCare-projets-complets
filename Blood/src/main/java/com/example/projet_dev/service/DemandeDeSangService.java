package com.example.projet_dev.service;

import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.DemandeDeSang;
import com.example.projet_dev.entity.Notification;
import com.example.projet_dev.entity.Stock;
import com.example.projet_dev.repository.DemandeDeSangRepository;
import com.example.projet_dev.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DemandeDeSangService {

    private final DemandeDeSangRepository repository;
    private final HopitalService hopitalService;
    private final CentreDeDonService centreService;
    private final StockRepository stockRepository;
    private final NotificationService notificationService;

    public List<DemandeDeSang> findAll() {
        return repository.findAll();
    }

    public List<DemandeDeSang> findByStatut(String statut) {
        return repository.findByStatut(statut);
    }

    /** Hopital.suivreDemande() */
    public List<DemandeDeSang> findByHopital(Long hopitalId) {
        return repository.findByHopitalId(hopitalId);
    }

    /** CentreDeDon traite DemandeDeSang */
    public List<DemandeDeSang> findByCentre(Long centreId) {
        return repository.findByCentreDeDonId(centreId);
    }

    /** Demandes visibles par un centre : celles qu'il a traitées + les nouvelles demandes pas encore prises en charge */
    public List<DemandeDeSang> findPourCentre(Long centreId) {
        List<DemandeDeSang> list = new ArrayList<>(repository.findByCentreDeDonId(centreId));
        repository.findByStatut("EN_ATTENTE").stream()
                .filter(d -> d.getCentreDeDon() == null)
                .forEach(list::add);
        list.sort(Comparator.comparing(DemandeDeSang::getId).reversed());
        return list;
    }

    /**
     * CentreDeDon.repondreDemande()
     *  - ACCEPTEE : les poches fournies sont retirées du stock du centre
     *  - REFUSEE  : aucune poche fournie
     * L'hôpital reçoit une notification dans les deux cas.
     */
    @Transactional
    public DemandeDeSang repondre(Long centreId, Long id, String statut, Integer unitesFournies) {
        DemandeDeSang d = findById(id);
        if (!"EN_ATTENTE".equals(d.getStatut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette demande a déjà été traitée");
        }
        if (d.getCentreDeDon() != null && !d.getCentreDeDon().getId().equals(centreId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Demande prise en charge par un autre centre");
        }
        CentreDeDon centre = centreService.findById(centreId);

        if ("ACCEPTEE".equals(statut)) {
            int unites = unitesFournies == null ? d.getQuantite() : unitesFournies;
            if (unites < 1 || unites > d.getQuantite()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le nombre de poches doit être entre 1 et " + d.getQuantite());
            }
            Stock stock = stockRepository.findByCentreDeDonId(centreId).stream()
                    .filter(s -> Objects.equals(s.getGroupeSanguin(), d.getGroupeSanguin()))
                    .findFirst()
                    .orElse(null);
            int disponible = stock == null ? 0 : stock.getQuantiteDisponible();
            if (disponible < unites) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuffisant en " + d.getGroupeSanguin() + " (" + disponible + " poche(s) disponible(s))");
            }
            stock.setQuantiteDisponible(disponible - unites);
            stockRepository.save(stock);
            d.setUnitesFournies(unites);
        } else if ("REFUSEE".equals(statut)) {
            d.setUnitesFournies(0);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide (ACCEPTEE ou REFUSEE)");
        }

        d.setStatut(statut);
        d.setCentreDeDon(centre);
        if (d.getDateCreation() != null) {
            // délai de traitement en heures
            d.setDelaiTraitement(Duration.between(d.getDateCreation(), LocalDateTime.now()).toMinutes() / 60f);
        }
        DemandeDeSang saved = repository.save(d);
        notifierHopital(saved, centre);
        return saved;
    }

    public DemandeDeSang findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));
    }

    /** Hopital.envoyerDemande() */
    public DemandeDeSang envoyer(Long hopitalId, DemandeDeSang demande) {
        demande.setId(null);
        demande.setHopital(hopitalService.findById(hopitalId));
        // champs gérés par le serveur / le centre de don, jamais par l'hôpital
        demande.setStatut("EN_ATTENTE");
        demande.setCentreDeDon(null);
        demande.setUnitesFournies(0);
        demande.setDelaiTraitement(null);
        demande.setDateCreation(null);
        return repository.save(demande);
    }

    public DemandeDeSang update(Long id, DemandeDeSang data) {
        DemandeDeSang d = findById(id);
        d.setGroupeSanguin(data.getGroupeSanguin());
        d.setComposant(data.getComposant());
        d.setQuantite(data.getQuantite());
        d.setUrgence(data.getUrgence());
        d.setMotif(data.getMotif());
        d.setStatut(data.getStatut());
        d.setUnitesFournies(data.getUnitesFournies());
        d.setDelaiTraitement(data.getDelaiTraitement());
        return repository.save(d);
    }

    public DemandeDeSang changerStatut(Long id, String statut) {
        DemandeDeSang d = findById(id);
        d.setStatut(statut);
        return repository.save(d);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private void notifierHopital(DemandeDeSang d, CentreDeDon centre) {
        if (d.getHopital() == null) return;
        Notification n = new Notification();
        n.setType("DEMANDE");
        n.setMessage("ACCEPTEE".equals(d.getStatut())
                ? "Votre demande #" + d.getId() + " (" + d.getGroupeSanguin() + ") a été acceptée par " + centre.getNom()
                  + " : " + d.getUnitesFournies() + "/" + d.getQuantite() + " poche(s) fournie(s)."
                : "Votre demande #" + d.getId() + " (" + d.getGroupeSanguin() + ") a été refusée par " + centre.getNom() + ".");
        notificationService.envoyer(d.getHopital().getId(), n);
    }
}
