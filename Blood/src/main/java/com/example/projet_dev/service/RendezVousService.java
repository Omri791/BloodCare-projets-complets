package com.example.projet_dev.service;

import com.example.projet_dev.entity.Don;
import com.example.projet_dev.entity.Donneur;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.repository.DonneurRepository;
import com.example.projet_dev.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RendezVousService {

    private final RendezVousRepository repository;
    private final DonneurService donneurService;
    private final DonneurRepository donneurRepository;
    private final CentreDeDonService centreService;

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

    /** Donneur.prendreRendezVous() — body peut contenir "centreDeDon": {"id": 1} */
    public RendezVous prendre(Long donneurId, RendezVous rdv) {
        if (!donneurService.verifierEligibilite(donneurId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Le donneur n'est pas éligible au don");
        }
        rdv.setId(null);
        rdv.setDon(null);
        rdv.setStatut("PLANIFIE");
        rdv.setDonneur(donneurService.findById(donneurId));
        if (rdv.getCentreDeDon() != null && rdv.getCentreDeDon().getId() != null) {
            rdv.setCentreDeDon(centreService.findById(rdv.getCentreDeDon().getId()));
        } else {
            rdv.setCentreDeDon(null);
        }
        return repository.save(rdv);
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

    /** RendezVous aboutit à Don : met à jour l'historique du donneur */
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

        return repository.save(r);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}