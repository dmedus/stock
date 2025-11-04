package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.entidades.Variedad;

public interface VariedadRepository extends JpaRepository<Variedad, Long> {

}