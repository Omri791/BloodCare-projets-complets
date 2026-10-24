package com.example.projet_dev.service;


import com.example.projet_dev.entity.Admin;
import com.example.projet_dev.repository.AdminRepository;
import com.example.projet_dev.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Admin> findAll() {
        return repository.findAll();
    }

    public Admin findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin introuvable"));
    }

    public Admin create(Admin admin) {
        if (utilisateurRepository.existsByEmail(admin.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        admin.setId(null);
        return repository.save(admin);
    }

    public Admin update(Long id, Admin data) {
        Admin admin = findById(id);
        admin.setNom(data.getNom());
        admin.setEmail(data.getEmail());
        if (data.getMotDePasse() != null) admin.setMotDePasse(data.getMotDePasse());
        return repository.save(admin);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
