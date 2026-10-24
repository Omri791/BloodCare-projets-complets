package com.example.projet_dev.controller;



import com.example.projet_dev.entity.DemandeDeSang;
import com.example.projet_dev.service.DemandeDeSangService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeDeSangController {

    private final DemandeDeSangService service;

    @GetMapping
    public List<DemandeDeSang> getAll(@RequestParam(required = false) String statut) {
        return statut == null ? service.findAll() : service.findByStatut(statut);
    }

    @GetMapping("/{id}")
    public DemandeDeSang getById(@PathVariable Long id) {
        return service.findById(id);
    }

    // creation: POST /api/hopitaux/{id}/demandes

    @PutMapping("/{id}")
    public DemandeDeSang update(@PathVariable Long id, @RequestBody DemandeDeSang demande) {
        return service.update(id, demande);
    }

    /** body: {"statut": "ACCEPTEE"} */
    @PatchMapping("/{id}/statut")
    public DemandeDeSang changerStatut(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.changerStatut(id, body.get("statut"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
