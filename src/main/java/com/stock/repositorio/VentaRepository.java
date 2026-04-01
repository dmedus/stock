package com.stock.repositorio;

import com.stock.entidades.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT SUM(v.total) FROM Venta v")
    BigDecimal sumTotalVentas();

    List<Venta> findTop5ByOrderByFechaDesc();

    List<Venta> findByActivoTrue();

    @Query("SELECT v FROM Venta v WHERE " +
           "(:fechaInicio IS NULL OR v.fecha >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR v.fecha <= :fechaFin) AND " +
           "(:clienteNombre IS NULL OR LOWER(v.cliente.nombre) LIKE LOWER(CONCAT('%', :clienteNombre, '%')) OR LOWER(v.cliente.apellido) LIKE LOWER(CONCAT('%', :clienteNombre, '%'))) AND " +
           "(:activo IS NULL OR v.activo = :activo)")
    List<Venta> searchVentas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin, @Param("clienteNombre") String clienteNombre, @Param("activo") Boolean activo);

    /**
     * Resumen de ventas del mes: [totalIngresos, totalCobrado, cantidadVentas, clientesUnicos]
     */
    @Query("SELECT COALESCE(SUM(v.total), 0), COALESCE(SUM(v.pagado), 0), COUNT(v), COUNT(DISTINCT v.cliente.id) " +
           "FROM Venta v WHERE v.activo = true AND YEAR(v.fecha) = :year AND MONTH(v.fecha) = :month")
    Object[] getResumenMes(@Param("year") int year, @Param("month") int month);

    /**
     * Cantidad de ventas entregadas en el mes.
     */
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.activo = true AND v.entregado = true AND YEAR(v.fecha) = :year AND MONTH(v.fecha) = :month")
    Long countEntregadasMes(@Param("year") int year, @Param("month") int month);

    /**
     * Años distintos con ventas registradas, para el selector del informe.
     */
    @Query("SELECT DISTINCT YEAR(v.fecha) FROM Venta v WHERE v.activo = true ORDER BY YEAR(v.fecha) DESC")
    List<Integer> findAniosConVentas();
}
