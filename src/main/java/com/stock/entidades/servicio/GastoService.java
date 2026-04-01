package com.stock.entidades.servicio;

import com.stock.entidades.Gasto;

import java.time.LocalDate;
import java.util.List;

public interface GastoService {

    List<Gasto> listarTodos();

    List<Gasto> listarPorMes(LocalDate inicio, LocalDate fin);

    Gasto findById(Long id);

    void save(Gasto gasto);

    void deleteById(Long id);
}
