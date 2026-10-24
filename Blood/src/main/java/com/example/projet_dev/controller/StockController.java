package com.example.projet_dev.controller;


import com.example.projet_dev.entity.Stock;
import com.example.projet_dev.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService service;

    @GetMapping
    public List<Stock> getAll(@RequestParam(required = false) String groupeSanguin) {
        return groupeSanguin == null ? service.findAll() : service.findByGroupeSanguin(groupeSanguin);
    }

    @GetMapping("/{id}")
    public Stock getById(@PathVariable Long id) {
        return service.findById(id);
    }

    /** body: {"groupeSanguin":"O+","quantiteDisponible":20,"centreDeDon":{"id":1}} */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Stock create(@RequestBody Stock stock) {
        return service.create(stock);
    }

    @PutMapping("/{id}")
    public Stock update(@PathVariable Long id, @RequestBody Stock stock) {
        return service.update(id, stock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
