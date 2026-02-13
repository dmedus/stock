package com.stock.entidades.servicio;

import com.stock.entidades.Deposito;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;
import com.stock.repositorio.VinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class VinoServiceImpl implements VinoService {

    @Autowired
    private VinoRepository vinoRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private DepositoService depositoService;

    @Override
    @Transactional(readOnly = true)
    public List<Vino> findAll() {
        List<Vino> vinos = vinoRepository.findAll();
        vinos.forEach(vino -> vino.setStockActual(stockService.getStockTotal(vino)));
        return vinos;
    }

    @Override
    @Transactional(readOnly = true)
    public Vino findById(Long id) {
        Vino vino = vinoRepository.findById(id).orElse(null);
        if (vino != null) {
            vino.setStockActual(stockService.getStockTotal(vino));
        }
        return vino;
    }

    @Override
    @Transactional
    public void save(Vino vino) {
        vinoRepository.save(vino);

        if (vino.getStockActual() != null) {
            List<Deposito> depositos = depositoService.findAll();
            Deposito deposito;
            
            if (depositos.isEmpty()) {
                deposito = new Deposito();
                deposito.setNombre("Depósito Principal");
                deposito.setDireccion("Dirección por defecto");
                depositoService.save(deposito);
            } else {
                deposito = depositos.get(0);
            }

            Stock stock = stockService.findByVinoAndDeposito(vino, deposito);
            if (stock == null) {
                stock = new Stock();
                stock.setVino(vino);
                stock.setDeposito(deposito);
            }
            stock.setCantidad(vino.getStockActual());
            stockService.save(stock);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        vinoRepository.deleteById(id);
    }

    @Override
    public List<Vino> findByNombre(String term) {
        String normalizedTerm = Normalizer.normalize(term, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String accentFreeTerm = pattern.matcher(normalizedTerm).replaceAll("");
        List<Vino> vinos = vinoRepository.findByNombreContainingIgnoreCase(accentFreeTerm);
        vinos.forEach(vino -> vino.setStockActual(stockService.getStockTotal(vino)));
        return vinos;
    }

    @Override
    public long countAll() {
        return vinoRepository.count();
    }

    @Override
    public List<Vino> findTop5ByOrderByStockActualAsc() {
        List<Vino> vinos = vinoRepository.findTop5ByOrderByStockActualAsc();
        vinos.forEach(vino -> vino.setStockActual(stockService.getStockTotal(vino)));
        return vinos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vino> findVinosBelowMinStock() {
        List<Vino> allVinos = findAll(); // This already populates stockActual
        return allVinos.stream()
                .filter(vino -> vino.getStockActual() != null && vino.getStockMinimo() != null && vino.getStockActual() < vino.getStockMinimo())
                .collect(java.util.stream.Collectors.toList());
    }
}