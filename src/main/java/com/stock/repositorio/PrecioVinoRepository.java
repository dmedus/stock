package com.stock.repositorio;

import com.stock.entidades.ListaPrecio;
import com.stock.entidades.Vino; // Added import
import org.springframework.data.jpa.repository.JpaRepository;
import com.stock.entidades.PrecioVino;

import java.util.List; // Added import

public interface PrecioVinoRepository extends JpaRepository<PrecioVino, Long> {
    List<PrecioVino> findByVino(Vino vino); // Added method
    PrecioVino findByVinoAndListaPrecio(Vino vino, ListaPrecio listaPrecio);
    List<PrecioVino> findByListaPrecio(ListaPrecio listaPrecio);
}
