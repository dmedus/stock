package com.stock.entidades.servicio;

import com.stock.entidades.VentaDetalle;
import com.stock.repositorio.VentaDetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaDetalleServiceImpl implements VentaDetalleService {

    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VentaDetalle> findAll() {
        return ventaDetalleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDetalle findById(Long id) {
        return ventaDetalleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(VentaDetalle ventaDetalle) {
        ventaDetalleRepository.save(ventaDetalle);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ventaDetalleRepository.deleteById(id);
    }
}
