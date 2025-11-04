package com.stock.entidades.servicio;

import com.stock.entidades.Bodega;
import java.util.List;

public interface BodegaService {
    public List<Bodega> findAll();
    public Bodega findById(Long id);
    public void save(Bodega bodega);
    public void deleteById(Long id);
}