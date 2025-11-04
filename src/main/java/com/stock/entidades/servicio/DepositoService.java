package com.stock.entidades.servicio;

import com.stock.entidades.Deposito;
import java.util.List;

public interface DepositoService {
    public List<Deposito> findAll();
    public Deposito findById(Long id);
    public void save(Deposito deposito);
    public void deleteById(Long id);
}
