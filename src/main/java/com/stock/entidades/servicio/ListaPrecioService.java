package com.stock.entidades.servicio;

import com.stock.entidades.ListaPrecio;
import java.util.List;

public interface ListaPrecioService {
    public List<ListaPrecio> findAll();
    public ListaPrecio findById(Long id);
    public void save(ListaPrecio listaPrecio);
    public void deleteById(Long id);
}
