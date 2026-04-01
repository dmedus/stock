package com.stock.repositorio;

import com.stock.entidades.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByFechaBetweenOrderByFechaDesc(LocalDate inicio, LocalDate fin);

    List<Gasto> findAllByOrderByFechaDesc();
}
