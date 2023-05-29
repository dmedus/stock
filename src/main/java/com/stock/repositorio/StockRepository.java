package com.stock.repositorio;


import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stock.entidades.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>{

	@Query("SELECT s FROM Stock s  inner join s.modelo m  WHERE (m.nombre LIKE %?1% "+ "OR s.imei LIKE %?1%" + "OR s.proveedor LIKE %?1%" + "OR s.lugar LIKE %?1% )" +  "AND s.inStock =?2")
	public Page<Stock> findBypalabraClave(String palabraClave,boolean inStock,Pageable pageable);
	
	@Query("SELECT s FROM Stock s  inner join s.modelo m  WHERE (m.nombre LIKE %?1% "+ "OR s.imei LIKE %?1%" + "OR s.proveedor LIKE %?1%" + "OR s.lugar LIKE %?1% )" +  "AND s.inStock =?2 " + "AND s.fecha between ?3 and ?4 ")
	public Page<Stock> findAllPage(String palabraClave,boolean inStock, Date fecha, Date fecha2, Pageable pageable);
}
