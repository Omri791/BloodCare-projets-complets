package com.example.projet_dev.controller;


import com.example.projet_dev.entity.Utilisateur;
import com.example.projet_dev.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService service;

    @GetMapping
    public List<Utilisateur> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Utilisateur getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** body: {"email": "...", "motDePasse": "..."} */
    @PostMapping("/login")
    public Utilisateur login(@RequestBody Map<String, String> body) {
        return service.authentifier(body.get("email"), body.get("motDePasse"));
    }
}
