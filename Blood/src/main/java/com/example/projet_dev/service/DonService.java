package com.example.projet_dev.service;

import com.example.projet_dev.entity.Don;
import com.example.projet_dev.repository.DonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonService {

    private final DonRepository repository;

    public List<Don> findAll() {
        return repository.findAll();
    }

    public Don findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Don introuvable"));
    }

    public Don create(Don don) {
        don.setId(null);
        return repository.save(don);
    }

    public Don update(Long id, Don data) {
        Don d = findById(id);
        d.setDateDon(data.getDateDon());
        d.setQuantite(data.getQuantite());
        return repository.save(d);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}