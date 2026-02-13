package com.stock.entidades.servicio;

import com.stock.entidades.Venta;
import com.stock.entidades.VentaDetalle;
import com.stock.entidades.Vino;
import com.stock.repositorio.VentaRepository;
import com.stock.repositorio.VinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VinoRepository vinoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Long id) {
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
    public BigDecimal sumTotalVentas() {
        return ventaRepository.sumTotalVentas();
    }

    @Override
    public List<Venta> findTop5ByOrderByFechaDesc() {
        return ventaRepository.findTop5ByOrderByFechaDesc();
    }
}
