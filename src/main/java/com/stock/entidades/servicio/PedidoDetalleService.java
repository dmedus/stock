package com.stock.entidades.servicio;

import com.stock.entidades.PedidoDetalle;
import java.util.List;

public interface PedidoDetalleService {
    public List<PedidoDetalle> findAll();
    public PedidoDetalle findById(Long id);
    public void save(PedidoDetalle pedidoDetalle);
    public void deleteById(Long id);
}
