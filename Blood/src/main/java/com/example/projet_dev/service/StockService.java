package com.example.projet_dev.service;


import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.Stock;
import com.example.projet_dev.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository repository;
    private final CentreDeDonService centreService;

    public List<Stock> findAll() {
        return repository.findAll();
    }

    public List<Stock> findByGroupeSanguin(String groupeSanguin) {
        return repository.findByGroupeSanguin(groupeSanguin);
    }

    /** CentreDeDon.gererStock() */
    public List<Stock> findByCentre(Long centreId) {
        return repository.findByCentreDeDonId(centreId);
    }

    /**
     * CentreDeDon.gererStock() : fixe le nombre de poches d'un groupe sanguin
     * (inventaire, poches périmées...). La ligne de stock est créée si elle n'existe pas.
     */
    public Stock definirQuantite(Long centreId, String groupeSanguin, Integer quantite) {
        if (!GROUPES.contains(groupeSanguin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Groupe sanguin invalide");
        }
        if (quantite == null || quantite < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La quantité ne peut pas être négative");
        }
        CentreDeDon centre = centreService.findById(centreId);
        List<Stock> stocks = repository.findByCentreDeDonId(centreId);

        if (centre.getCapaciteStock() != null) {
            int autres = stocks.stream()
                    .filter(s -> !groupeSanguin.equals(s.getGroupeSanguin()))
                    .mapToInt(Stock::getQuantiteDisponible)
                    .sum();
            if (autres + quantite > centre.getCapaciteStock()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Capacité du centre dépassée (" + centre.getCapaciteStock() + " poches maximum)");
            }
        }

        Stock stock = stocks.stream()
                .filter(s -> groupeSanguin.equals(s.getGroupeSanguin()))
                .findFirst()
                .orElseGet(() -> {
                    Stock s = new Stock();
                    s.setGroupeSanguin(groupeSanguin);
                    s.setCentreDeDon(centre);
                    return s;
                });
        stock.setQuantiteDisponible(quantite);
        return repository.save(stock);
    }

    private static final List<String> GROUPES = List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    public Stock findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock introuvable"));
    }

    /** Body may contain "centreDeDon": {"id": 1} */
    public Stock create(Stock stock) {
        stock.setId(null);
        stock.setCentreDeDon(resolveCentre(stock.getCentreDeDon()));
        return repository.save(stock);
    }

    public Stock update(Long id, Stock data) {
        Stock s = findById(id);
        s.setGroupeSanguin(data.getGroupeSanguin());
        s.setQuantiteDisponible(data.getQuantiteDisponible());
        if (data.getCentreDeDon() != null) s.setCentreDeDon(resolveCentre(data.getCentreDeDon()));
        return repository.save(s);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private CentreDeDon resolveCentre(CentreDeDon c) {
        if (c == null || c.getId() == null) return null;
        return centreService.findById(c.getId());
    }
}
