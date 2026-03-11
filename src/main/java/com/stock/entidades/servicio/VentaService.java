package com.stock.entidades.servicio;

import com.stock.entidades.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VentaService {

    public List<Venta> findAll();

    public List<Venta> findByActivoTrue();

    public List<Venta> searchVentas(LocalDate fechaInicio, LocalDate fechaFin, String clienteNombre, Boolean activo);

    public Venta findById(Long id);

    public void save(Venta venta);

    public void deleteById(Long id);

    public long countAll();

    public BigDecimal sumTotalVentas();

    public List<Venta> findTop5ByOrderByFechaDesc();
}
