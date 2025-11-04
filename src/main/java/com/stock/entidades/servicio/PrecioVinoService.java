package com.stock.entidades.servicio;

import com.stock.entidades.ListaPrecio;
import com.stock.entidades.PrecioVino;
import java.util.List;

import com.stock.entidades.Vino; // Added import
import java.math.BigDecimal; // Added import
import java.io.InputStream; // Added import

public interface PrecioVinoService {
    public List<PrecioVino> findAll();
    public PrecioVino findById(Long id);
    public void save(PrecioVino precioVino);
    public void deleteById(Long id);
    public BigDecimal findPrecioByVino(Vino vino); // Added method
    public BigDecimal findPrecioByVinoAndListaPrecio(Vino vino, ListaPrecio listaPrecio);
    public void importarPreciosDesdeExcel(InputStream is);
    public List<PrecioVino> findByListaPrecio(ListaPrecio listaPrecio);
}
