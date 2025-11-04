package com.stock.repositorio;

import com.stock.entidades.Deposito;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByVinoAndDeposito(Vino vino, Deposito deposito);
    List<Stock> findByVino(Vino vino);
}
