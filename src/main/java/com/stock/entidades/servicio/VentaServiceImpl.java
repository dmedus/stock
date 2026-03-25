package com.stock.entidades.servicio;

import com.stock.entidades.Combo;
import com.stock.entidades.Venta;
import com.stock.entidades.VentaDetalle;
import com.stock.entidades.Vino;
import com.stock.repositorio.VentaRepository;
import com.stock.repositorio.VinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VinoRepository vinoRepository;

    @Autowired
    private StockService stockService;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByActivoTrue() {
        return ventaRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> searchVentas(LocalDate fechaInicio, LocalDate fechaFin, String clienteNombre, Boolean activo) {
        return ventaRepository.searchVentas(fechaInicio, fechaFin, clienteNombre, activo);
    }

    @Override
    @Transactional(readOnly = true)    public Venta findById(Long id) {
        Venta venta = ventaRepository.findById(id).orElse(null);
        if(venta != null) {
        	venta.getDetalles().size();
        }
        return venta;
    }

    @Override
    @Transactional
    public void save(Venta venta) {
        ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }

    @Override
    public long countAll() {
        return ventaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumTotalVentas() {
        BigDecimal result = ventaRepository.sumTotalVentas();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<Venta> findTop5ByOrderByFechaDesc() {
        return ventaRepository.findTop5ByOrderByFechaDesc();
    }

    @Override
    @Transactional
    public void entregarVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("La venta no existe en la base de datos"));

        if (venta.getEntregado()) {
            throw new RuntimeException("La venta ya ha sido entregada");
        }

        for (VentaDetalle detalle : venta.getDetalles()) {
            if (detalle.getVino() != null) {
                int cantidadADescontar = detalle.getCantidad();
                if (detalle.getListaPrecio() != null) {
                    String nombreLista = detalle.getListaPrecio().getNombre().toLowerCase();
                    if (nombreLista.contains("caja") || nombreLista.contains("mayorista") || nombreLista.contains("bulto")) {
                        cantidadADescontar = detalle.getCantidad() * detalle.getVino().getCantVinosxcaja();
                    }
                }
                stockService.discountStock(detalle.getVino(), cantidadADescontar);
            } else if (detalle.getCombo() != null) {
                Combo combo = detalle.getCombo();
                for (Vino v : combo.getVinos()) {
                    stockService.discountStock(v, detalle.getCantidad());
                }
            }
        }

        venta.setEntregado(true);
        ventaRepository.save(venta);
    }
}
