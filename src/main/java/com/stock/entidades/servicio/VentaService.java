 package com.stock.entidades.servicio;

import java.util.List;

import com.stock.entidades.Venta;

public interface VentaService {


	public List<Venta> findAll();
	
	public void save(Venta venta);
	
	public Venta findOne(Long id);
	
	public void delete(Long id);
 
}
