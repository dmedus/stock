package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.entidades.PedidoDetalle;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {

}
