package com.example.projet_dev.service;

import com.example.projet_dev.entity.DemandeDeSang;
import com.example.projet_dev.repository.DemandeDeSangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeDeSangService {

    private final DemandeDeSangRepository repository;
    private final HopitalService hopitalService;
    private final CentreDeDonService centreService;

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

    /** CentreDeDon.repondreDemande() */
    public DemandeDeSang repondre(Long centreId, Long id, String statut) {
        DemandeDeSang d = findById(id);
        d.setCentreDeDon(centreService.findById(centreId));
        if (statut != null) d.setStatut(statut);
        return repository.save(d);
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
}
