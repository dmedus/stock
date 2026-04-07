package com.stock.repositorio;

import com.stock.entidades.PedidoDetalle;
import com.stock.entidades.Vino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    boolean existsByVino(Vino vino);
}
