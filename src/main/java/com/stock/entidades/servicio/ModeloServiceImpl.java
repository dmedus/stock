package com.stock.entidades.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.entidades.Modelo;
import com.stock.repositorio.ModeloRepository;

@Service 
public class ModeloServiceImpl implements ModeloService{

	@Autowired
	private ModeloRepository modeloRepository;
	
	@Override
	@Transactional(readOnly = true)
	public List<Modelo> findAll() {
		return modeloRepository.findAll();
	}

	@Override
	@Transactional
	public void save(Modelo modelo) {
		modeloRepository.save(modelo);
		
	}

	@Override
	@Transactional(readOnly = true)
	public Modelo findOne(Long id) {
		return modeloRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		modeloRepository.deleteById(id);		
	}



}
