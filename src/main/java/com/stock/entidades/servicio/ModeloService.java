 package com.stock.entidades.servicio;

import java.util.List;

import com.stock.entidades.Modelo;

public interface ModeloService {


	public List<Modelo> findAll();
	
	public void save(Modelo modelo);
	
	public Modelo findOne(Long id);
	
	public void delete(Long id); 
}
