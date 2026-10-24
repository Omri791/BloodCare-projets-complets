package com.example.projet_dev.controller;



import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.DemandeDeSang;
import com.example.projet_dev.entity.Prediction;
import com.example.projet_dev.entity.Stock;
import com.example.projet_dev.service.CentreDeDonService;
import com.example.projet_dev.service.DemandeDeSangService;
import com.example.projet_dev.service.PredictionService;
import com.example.projet_dev.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/centres")
@RequiredArgsConstructor
public class CentreDeDonController {

    private final CentreDeDonService service;
    private final StockService stockService;
    private final PredictionService predictionService;
    private final DemandeDeSangService demandeService;

    @GetMapping
    public List<CentreDeDon> getAll(@RequestParam(required = false) String ville) {
        return ville == null ? service.findAll() : service.findByVille(ville);
    }

    @GetMapping("/{id}")
    public CentreDeDon getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CentreDeDon create(@RequestBody CentreDeDon centre) {
        return service.create(centre);
    }

    @PutMapping("/{id}")
    public CentreDeDon update(@PathVariable Long id, @RequestBody CentreDeDon centre) {
        return service.update(id, centre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----- relations -----

    /** CentreDeDon.gererStock() */
    @GetMapping("/{id}/stocks")
    public List<Stock> getStocks(@PathVariable Long id) {
        return stockService.findByCentre(id);
    }

    /** CentreDeDon consulte Prediction */
    @GetMapping("/{id}/predictions")
    public List<Prediction> getPredictions(@PathVariable Long id) {
        return predictionService.findByCentre(id);
    }

    /** CentreDeDon traite DemandeDeSang */
    @GetMapping("/{id}/demandes")
    public List<DemandeDeSang> getDemandes(@PathVariable Long id) {
        return demandeService.findByCentre(id);
    }

    /** CentreDeDon.repondreDemande() — body: {"statut": "ACCEPTEE"} */
    @PatchMapping("/{id}/demandes/{demandeId}")
    public DemandeDeSang repondreDemande(@PathVariable Long id, @PathVariable Long demandeId,
                                         @RequestBody Map<String, String> body) {
        return demandeService.repondre(id, demandeId, body.get("statut"));
    }
}
