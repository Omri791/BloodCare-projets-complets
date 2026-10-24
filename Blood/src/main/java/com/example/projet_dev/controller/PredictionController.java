package com.example.projet_dev.controller;


import com.example.projet_dev.entity.Prediction;
import com.example.projet_dev.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService service;

    @GetMapping
    public List<Prediction> getAll(@RequestParam(required = false) String groupeSanguin) {
        return groupeSanguin == null ? service.findAll() : service.findByGroupeSanguin(groupeSanguin);
    }

    @GetMapping("/{id}")
    public Prediction getById(@PathVariable Long id) {
        return service.findById(id);
    }

    /** body: {"mois":10,"annee":2026,"groupeSanguin":"O+","quantitePrevue":50,"typeAlerte":"PENURIE","centreDeDon":{"id":1}} */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prediction create(@RequestBody Prediction prediction) {
        return service.create(prediction);
    }

    @PutMapping("/{id}")
    public Prediction update(@PathVariable Long id, @RequestBody Prediction prediction) {
        return service.update(id, prediction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
