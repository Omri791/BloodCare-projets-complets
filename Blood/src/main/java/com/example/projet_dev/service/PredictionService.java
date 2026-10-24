package com.example.projet_dev.service;


import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.Prediction;
import com.example.projet_dev.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository repository;
    private final CentreDeDonService centreService;

    public List<Prediction> findAll() {
        return repository.findAll();
    }

    public List<Prediction> findByGroupeSanguin(String groupeSanguin) {
        return repository.findByGroupeSanguin(groupeSanguin);
    }

    /** CentreDeDon consulte Prediction */
    public List<Prediction> findByCentre(Long centreId) {
        return repository.findByCentreDeDonId(centreId);
    }

    public Prediction findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prédiction introuvable"));
    }

    /** Body may contain "centreDeDon": {"id": 1} */
    public Prediction create(Prediction prediction) {
        prediction.setId(null);
        prediction.setCentreDeDon(resolveCentre(prediction.getCentreDeDon()));
        return repository.save(prediction);
    }

    public Prediction update(Long id, Prediction data) {
        Prediction p = findById(id);
        p.setMois(data.getMois());
        p.setAnnee(data.getAnnee());
        p.setGroupeSanguin(data.getGroupeSanguin());
        p.setQuantitePrevue(data.getQuantitePrevue());
        p.setTypeAlerte(data.getTypeAlerte());
        if (data.getCentreDeDon() != null) p.setCentreDeDon(resolveCentre(data.getCentreDeDon()));
        return repository.save(p);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private CentreDeDon resolveCentre(CentreDeDon c) {
        if (c == null || c.getId() == null) return null;
        return centreService.findById(c.getId());
    }
}
