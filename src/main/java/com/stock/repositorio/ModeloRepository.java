package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.entidades.Modelo;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long>{

}
