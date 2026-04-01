package com.stock.repositorio;

import com.stock.entidades.VentaDetalle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {

    /**
     * Totales de botellas vendidas, ingresos y costos para un mes/año dado.
     * Solo ventas activas con vino (excluye combos).
     * Retorna: [cantidad total, subtotal total, costo total]
     */
    @Query("SELECT COALESCE(SUM(vd.cantidad), 0), " +
           "COALESCE(SUM(vd.subtotal), 0), " +
           "COALESCE(SUM(vd.cantidad * (COALESCE(vd.vino.costoCompra, 0) + COALESCE(vd.vino.costoFlete, 0))), 0) " +
           "FROM VentaDetalle vd JOIN vd.venta v " +
           "WHERE v.activo = true AND vd.vino IS NOT NULL " +
           "AND YEAR(v.fecha) = :year AND MONTH(v.fecha) = :month")
    Object[] getTotalesDetallesMes(@Param("year") int year, @Param("month") int month);

    /**
     * Top vinos del mes agrupados por vino, ordenados por cantidad vendida desc.
     * Retorna por fila: [vinoId, nombre, cantidad, subtotal, costo]
     */
    @Query("SELECT vd.vino.id, vd.vino.nombre, SUM(vd.cantidad), SUM(vd.subtotal), " +
           "SUM(vd.cantidad * (COALESCE(vd.vino.costoCompra, 0) + COALESCE(vd.vino.costoFlete, 0))) " +
           "FROM VentaDetalle vd JOIN vd.venta v " +
           "WHERE v.activo = true AND vd.vino IS NOT NULL " +
           "AND YEAR(v.fecha) = :year AND MONTH(v.fecha) = :month " +
           "GROUP BY vd.vino.id, vd.vino.nombre " +
           "ORDER BY SUM(vd.cantidad) DESC")
    List<Object[]> findTopVinosPorMes(@Param("year") int year, @Param("month") int month, Pageable pageable);
}
