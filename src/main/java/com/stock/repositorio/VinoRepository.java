package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.stock.entidades.Vino;

import java.util.List;

public interface VinoRepository extends JpaRepository<Vino, Long> {
    List<Vino> findByNombreContainingIgnoreCase(String nombre);
    @Query(value = "SELECT v.* FROM vinos v LEFT JOIN stock s ON v.id = s.vino_id GROUP BY v.id ORDER BY SUM(s.cantidad) ASC LIMIT 5", nativeQuery = true)
    List<Vino> findTop5ByOrderByStockActualAsc();
}