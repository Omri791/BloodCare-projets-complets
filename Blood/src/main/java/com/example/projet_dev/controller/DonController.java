package com.example.projet_dev.controller;

import com.example.projet_dev.entity.Don;
import com.example.projet_dev.service.DonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dons")
@RequiredArgsConstructor
public class DonController {

    private final DonService service;

    @GetMapping
    public List<Don> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Don getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Don create(@RequestBody Don don) {
        return service.create(don);
    }

    @PutMapping("/{id}")
    public Don update(@PathVariable Long id, @RequestBody Don don) {
        return service.update(id, don);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}