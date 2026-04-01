package com.stock.repositorio;

import com.stock.entidades.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByFechaBetween(LocalDate inicio, LocalDate fin);
}
