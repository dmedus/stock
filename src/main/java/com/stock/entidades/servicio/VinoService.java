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
    public List<Vino> findVinosBelowMinStock();
    /**
     * Elimina el vino si no tiene historial de ventas ni pedidos.
     * Si tiene historial, lo desactiva (activo=false).
     * @return true si fue eliminado, false si fue desactivado.
     */
    public boolean eliminarODesactivar(Long id);
}