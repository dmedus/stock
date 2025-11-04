package com.stock.entidades.servicio;

import com.stock.entidades.Pedido; // Added import
import com.stock.entidades.PedidoDetalle;
import com.stock.repositorio.PedidoDetalleRepository;
import com.stock.repositorio.PedidoRepository; // Added import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // Added import
import java.util.List;
import java.util.Optional; // Added import

@Service
public class PedidoDetalleServiceImpl implements PedidoDetalleService {

    @Autowired
    private PedidoDetalleRepository pedidoDetalleRepository;

    @Autowired
    private PedidoRepository pedidoRepository; // Injected PedidoRepository

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDetalle> findAll() {
        return pedidoDetalleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDetalle findById(Long id) {
        return pedidoDetalleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(PedidoDetalle pedidoDetalle) {
        pedidoDetalleRepository.save(pedidoDetalle);

        // Recalculate and update Pedido total
        Pedido pedido = pedidoDetalle.getPedido();
        if (pedido != null) {
            BigDecimal total = BigDecimal.ZERO;
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                total = total.add(detalle.getSubtotal());
            }
            pedido.setTotal(total);
            pedidoRepository.save(pedido);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        pedidoDetalleRepository.deleteById(id);
    }
}
