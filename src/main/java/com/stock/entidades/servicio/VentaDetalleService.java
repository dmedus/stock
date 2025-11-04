package com.stock.entidades.servicio;

import com.stock.entidades.VentaDetalle;

import java.util.List;

public interface VentaDetalleService {

    public List<VentaDetalle> findAll();

    public VentaDetalle findById(Long id);

    public void save(VentaDetalle ventaDetalle);

    public void deleteById(Long id);
}
