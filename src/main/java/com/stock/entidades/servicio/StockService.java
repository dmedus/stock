package com.stock.entidades.servicio;

import com.stock.entidades.Deposito;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;

import java.util.List;
import java.util.Map;

public interface StockService {
    public Stock findByVinoAndDeposito(Vino vino, Deposito deposito);
    public List<Stock> findByVino(Vino vino);
    public void save(Stock stock);
    public Integer getStockTotal(Vino vino);
    public Map<Long, Integer> getStockTotalMap();
    public void discountStock(Vino vino, Integer cantidad);
    public void moverStock(Vino vino, Deposito origen, Deposito destino, Integer cantidad);
}
