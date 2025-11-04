package com.stock.entidades.servicio;

import com.stock.entidades.Variedad;
import java.util.List;

public interface VariedadService {
    public List<Variedad> findAll();
    public Variedad findById(Long id);
    public void save(Variedad variedad);
    public void deleteById(Long id);
}