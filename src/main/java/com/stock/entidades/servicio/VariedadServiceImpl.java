package com.stock.entidades.servicio;

import com.stock.entidades.Variedad;
import com.stock.repositorio.VariedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VariedadServiceImpl implements VariedadService {

    @Autowired
    private VariedadRepository variedadRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Variedad> findAll() {
        return variedadRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Variedad findById(Long id) {
        return variedadRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Variedad variedad) {
        variedadRepository.save(variedad);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        variedadRepository.deleteById(id);
    }
}