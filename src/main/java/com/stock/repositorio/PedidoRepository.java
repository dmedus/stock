package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.entidades.Pedido;

import java.util.List; // Added import

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByActivoTrue(); // Added method
    long countByActivoTrue();
}
