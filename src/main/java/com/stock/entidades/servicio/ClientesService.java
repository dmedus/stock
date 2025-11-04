 package com.stock.entidades.servicio;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stock.entidades.Clientes;

public interface ClientesService{

	public Clientes guardar(Clientes clientes);
	
	public List<Clientes> findAll(); 
	
	public Page<Clientes> findAll(Pageable pageable, String palabraClave);
	
	public Clientes findById(Long id);
	
	public void delete(Long id); 

	public List<Clientes> findByNombreOrApellido(String term);

	public long countAll();
	
}
