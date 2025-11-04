package com.stock.entidades.servicio;

import com.stock.entidades.ListaPrecio;
import com.stock.repositorio.ListaPrecioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListaPrecioServiceImpl implements ListaPrecioService {

    @Autowired
    private ListaPrecioRepository listaPrecioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ListaPrecio> findAll() {
        return listaPrecioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ListaPrecio findById(Long id) {
        return listaPrecioRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(ListaPrecio listaPrecio) {
        listaPrecioRepository.save(listaPrecio);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        listaPrecioRepository.deleteById(id);
    }
}
