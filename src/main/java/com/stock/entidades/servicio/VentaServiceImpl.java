package com.stock.entidades.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.entidades.Venta;
import com.stock.repositorio.VentaRepository;

@Service 
public class VentaServiceImpl implements VentaService{

	@Autowired
	private VentaRepository ventaRepository;
	
	@Override
	@Transactional(readOnly = true)
	public List<Venta> findAll() {
		return ventaRepository.findAll();
	}

	@Override
	@Transactional
	public void save(Venta venta) {
		ventaRepository.save(venta);
		
	}

	@Override
	@Transactional(readOnly = true)
	public Venta findOne(Long id) {
		return ventaRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		ventaRepository.deleteById(id);		
	}

}
