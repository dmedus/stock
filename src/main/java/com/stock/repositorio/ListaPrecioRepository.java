package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.entidades.ListaPrecio;

public interface ListaPrecioRepository extends JpaRepository<ListaPrecio, Long> {

}
