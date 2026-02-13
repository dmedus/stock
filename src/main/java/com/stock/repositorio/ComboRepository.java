package com.stock.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.entidades.Combo;
import java.util.List;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Long> {
    List<Combo> findByNombreContainingIgnoreCase(String term);
}
