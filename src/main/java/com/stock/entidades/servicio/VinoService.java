package com.stock.entidades.servicio;

import com.stock.entidades.Vino;
import java.util.List;

public interface VinoService {
    public List<Vino> findAll();
    public Vino findById(Long id);
    public void save(Vino vino);
    public void deleteById(Long id);
    public List<Vino> findByNombre(String term);
    public long countAll();
    public List<Vino> findTop5ByOrderByStockActualAsc();
}