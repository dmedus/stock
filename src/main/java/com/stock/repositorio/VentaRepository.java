package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.entidades.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long>{


}
