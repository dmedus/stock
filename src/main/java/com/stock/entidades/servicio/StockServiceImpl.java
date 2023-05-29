package com.stock.entidades.servicio;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stock.entidades.Stock;
import com.stock.repositorio.StockRepository;

@Service 
public class StockServiceImpl implements StockService{

	@Autowired
	private StockRepository stockRepository;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Stock> finAll(Pageable pageable) {
		return stockRepository.findAll(pageable);
	}


	@Override
	@Transactional
	public void save(Stock celular) {
		stockRepository.save(celular);
		
	}

	@Override
	@Transactional(readOnly = true)
	public Stock findOne(Long id) {
		return stockRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		stockRepository.deleteById(id);		
	}

	@Override
	public Page<Stock> findAllPage(String palabraClave,boolean inStock, Date fecha,Date fecha2, Pageable pageable) {
		return stockRepository.findAllPage(palabraClave,inStock,fecha,fecha2,pageable);
	}


	@Override
	public List<Stock> findAll() {
		return stockRepository.findAll();
	}


	@Override
	public Page<Stock> findBypalabraClave(String palabraClave, boolean inStock, Pageable pageable) {
		return stockRepository.findBypalabraClave(palabraClave,inStock,pageable);
	}


	
	
	
	
	

}
