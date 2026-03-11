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
}
