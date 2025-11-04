package com.stock.repositorio;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.entidades.Clientes;


@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Long>{

    List<Clientes> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    @Query("SELECT c FROM Clientes c WHERE " +
            "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR " +
            "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR " +
            "LOWER(c.telefono) LIKE LOWER(CONCAT('%', :palabraClave, '%')) OR " +
            "LOWER(c.direccion) LIKE LOWER(CONCAT('%', :palabraClave, '%'))")
    Page<Clientes> findAll(@Param("palabraClave") String palabraClave, Pageable pageable);

}
