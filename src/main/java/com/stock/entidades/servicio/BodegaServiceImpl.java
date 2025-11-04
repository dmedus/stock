package com.stock.entidades.servicio;

import com.stock.entidades.Bodega;
import com.stock.repositorio.BodegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BodegaServiceImpl implements BodegaService {

    @Autowired
    private BodegaRepository bodegaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Bodega> findAll() {
        return bodegaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Bodega findById(Long id) {
        return bodegaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Bodega bodega) {
        bodegaRepository.save(bodega);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        bodegaRepository.deleteById(id);
    }
}