package com.stock.entidades.servicio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.entidades.Deposito;
import com.stock.entidades.Pedido;
import com.stock.entidades.PedidoDetalle;
import com.stock.entidades.Stock;
import com.stock.entidades.Vino;
import com.stock.repositorio.PedidoRepository;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private StockService stockService;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Pedido pedido) {
        // Calculate total before saving
        BigDecimal total = BigDecimal.ZERO;
        if (pedido.getDetalles() != null) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                total = total.add(detalle.getSubtotal());
            }
        }
        pedido.setTotal(total);
        pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        pedidoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void confirmarPedido(Long pedidoId) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        if (optionalPedido.isPresent()) {
            Pedido pedido = optionalPedido.get();
            Deposito deposito = pedido.getDeposito();

            if (deposito == null) {
                throw new RuntimeException("El pedido no tiene un depósito asignado.");
            }

            for (PedidoDetalle detalle : pedido.getDetalles()) {
                Vino vino = detalle.getVino();
                if (vino != null) {
                    Integer cantidad = detalle.getCantidad();
                    Stock stock = stockService.findByVinoAndDeposito(vino, deposito);
                    if (stock != null) {
                        stock.setCantidad(stock.getCantidad() + cantidad);
                    } else {
                        stock = new Stock();
                        stock.setVino(vino);
                        stock.setDeposito(deposito);
                        stock.setCantidad(cantidad);
                    }
                    stockService.save(stock);
                }
            }
            pedido.setActivo(false);
            pedidoRepository.save(pedido);
        } else {
            throw new RuntimeException("Pedido no encontrado con ID: " + pedidoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findActivePedidos() {
        return pedidoRepository.findByActivoTrue();
    }

    @Override
    public long countActivePedidos() {
        return pedidoRepository.countByActivoTrue();
    }
}
