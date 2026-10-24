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
