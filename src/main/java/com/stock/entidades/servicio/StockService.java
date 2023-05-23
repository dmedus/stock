 package com.stock.entidades.servicio;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stock.entidades.Stock;

public interface StockService {


	public List<Stock> findAll(String palabraClave);
	
	public Page<Stock> finAll(Pageable pageable);
	
	public Page<Stock> findAllPage(String palabraClave,boolean inStock,Pageable pageable);
	
	public void save(Stock stock);
	
	public Stock findOne(Long id);
	
	public void delete(Long id); 
}
