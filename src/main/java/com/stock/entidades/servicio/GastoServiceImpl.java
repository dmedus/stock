package com.stock.entidades.servicio;

import com.stock.entidades.Gasto;
import com.stock.repositorio.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GastoServiceImpl implements GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Gasto> listarTodos() {
        return gastoRepository.findAllByOrderByFechaDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Gasto> listarPorMes(LocalDate inicio, LocalDate fin) {
        return gastoRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public Gasto findById(Long id) {
        return gastoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Gasto gasto) {
        gastoRepository.save(gasto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        gastoRepository.deleteById(id);
    }
}
