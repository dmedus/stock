package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.entidades.Bodega;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {

}