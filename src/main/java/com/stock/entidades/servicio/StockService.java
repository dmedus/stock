 package com.stock.entidades.servicio;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stock.entidades.Stock;

public interface StockService {

	public List<Stock> findAll();
	
	public Page<Stock> finAll(Pageable pageable);
	
	public Page<Stock> findAllPage(String palabraClave,boolean inStock,Date fecha,Date fecha2, Pageable pageable);
	
	public void save(Stock stock);
	
	public Stock findOne(Long id);
	
	public void delete(Long id); 
	
	public Page<Stock> findBypalabraClave(String palabraClave,boolean inStock,Pageable pageable);
}
