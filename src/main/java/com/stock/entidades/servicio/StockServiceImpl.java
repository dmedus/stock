package com.stock.entidades.servicio;

import com.stock.entidades.Deposito;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;
import com.stock.repositorio.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Override
    @Transactional(readOnly = true)
    public Stock findByVinoAndDeposito(Vino vino, Deposito deposito) {
        return stockRepository.findByVinoAndDeposito(vino, deposito);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stock> findByVino(Vino vino) {
        return stockRepository.findByVino(vino);
    }

    @Override
    @Transactional
    public void save(Stock stock) {
        stockRepository.save(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getStockTotal(Vino vino) {
        List<Stock> stocks = stockRepository.findByVino(vino);
        return stocks.stream().mapToInt(Stock::getCantidad).sum();
    }

    @Override
    @Transactional
    public void discountStock(Vino vino, Integer cantidad) {
        Integer stockTotal = getStockTotal(vino);
        if (stockTotal < cantidad) {
            throw new RuntimeException("No hay suficiente stock para el vino: " + vino.getNombre());
        }

        List<Stock> stocks = stockRepository.findByVino(vino);
        int cantidadRestante = cantidad;

        for (Stock stock : stocks) {
            if (cantidadRestante == 0) {
                break;
            }

            if (stock.getCantidad() >= cantidadRestante) {
                stock.setCantidad(stock.getCantidad() - cantidadRestante);
                cantidadRestante = 0;
                stockRepository.save(stock);
            } else {
                cantidadRestante -= stock.getCantidad();
                stock.setCantidad(0);
                stockRepository.save(stock);
            }
        }
    }
}
