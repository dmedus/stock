package com.stock.entidades.servicio;

import com.stock.entidades.Deposito;
import com.stock.repositorio.DepositoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepositoServiceImpl implements DepositoService {

    @Autowired
    private DepositoRepository depositoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Deposito> findAll() {
        return depositoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Deposito findById(Long id) {
        return depositoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Deposito deposito) {
        depositoRepository.save(deposito);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        depositoRepository.deleteById(id);
    }
}
