package com.stock.entidades.servicio;

import com.stock.entidades.Pedido;
import java.util.List;

public interface PedidoService {
    public List<Pedido> findAll();
    public Pedido findById(Long id);
    public void save(Pedido pedido);
    public void deleteById(Long id);
    public void confirmarPedido(Long pedidoId); // Business logic for confirming order
    public List<Pedido> findActivePedidos(); // Added method
    public long countActivePedidos();
}
