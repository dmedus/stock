package com.stock.entidades.servicio;

import com.stock.entidades.Deposito;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;
import com.stock.repositorio.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Transactional(readOnly = true)
    public Map<Long, Integer> getStockTotalMap() {
        Map<Long, Integer> map = new HashMap<>();
        for (Object[] row : stockRepository.findStockTotalPorVino()) {
            Long vinoId = (Long) row[0];
            Integer total = ((Number) row[1]).intValue();
            map.put(vinoId, total);
        }
        return map;
    }

    @Override
    @Transactional
    public void moverStock(Vino vino, Deposito origen, Deposito destino, Integer cantidad) {
        Stock stockOrigen = stockRepository.findByVinoAndDeposito(vino, origen);
        if (stockOrigen == null || stockOrigen.getCantidad() < cantidad) {
            throw new RuntimeException("No hay suficiente stock en el depósito de origen.");
        }
        stockOrigen.setCantidad(stockOrigen.getCantidad() - cantidad);
        stockRepository.save(stockOrigen);

        Stock stockDestino = stockRepository.findByVinoAndDeposito(vino, destino);
        if (stockDestino == null) {
            stockDestino = new Stock();
            stockDestino.setVino(vino);
            stockDestino.setDeposito(destino);
            stockDestino.setCantidad(cantidad);
        } else {
            stockDestino.setCantidad(stockDestino.getCantidad() + cantidad);
        }
        stockRepository.save(stockDestino);
    }

    @Override
    @Transactional
    public void discountStock(Vino vino, Integer cantidad) {
        List<Stock> stocks = stockRepository.findByVinoForUpdate(vino);
        Integer stockTotal = stocks.stream().mapToInt(Stock::getCantidad).sum();

        if (stockTotal < cantidad) {
            throw new RuntimeException("No hay suficiente stock para el vino: " + vino.getNombre());
        }

        int cantidadRestante = cantidad;

        for (Stock stock : stocks) {
            if (cantidadRestante == 0) {
                break;
            }

            System.out.println("Procesando stock ID: " + stock.getId() + ", Cantidad en depósito: " + stock.getCantidad());

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
