package com.stock.repositorio;

import com.stock.entidades.Deposito;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByVinoAndDeposito(Vino vino, Deposito deposito);
    List<Stock> findByVino(Vino vino);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.vino = :vino")
    List<Stock> findByVinoForUpdate(@Param("vino") Vino vino);

    @Query("SELECT s.vino.id, SUM(s.cantidad) FROM Stock s GROUP BY s.vino.id")
    List<Object[]> findStockTotalPorVino();
}
