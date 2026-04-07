package com.stock.repositorio;

import com.stock.entidades.VentaDetalle;
import com.stock.entidades.Vino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {
    boolean existsByVino(Vino vino);
}
