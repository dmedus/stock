package com.stock.entidades.servicio;

import com.stock.entidades.Venta;

import java.math.BigDecimal;
import java.util.List;

public interface VentaService {

    public List<Venta> findAll();

    public Venta findById(Long id);

    public void save(Venta venta);

    public void deleteById(Long id);

    public long countAll();

    public BigDecimal sumTotalVentas();

    public List<Venta> findTop5ByOrderByFechaDesc();
}
